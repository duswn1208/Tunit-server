# Firebase Cloud Messaging (FCM) 연동 가이드

## 1. Firebase 프로젝트 생성

1. [Firebase Console](https://console.firebase.google.com/) 접속
2. "프로젝트 추가" 클릭
3. 프로젝트 이름 입력 (예: Tunit)
4. Google Analytics 설정 (선택사항)

## 2. Firebase Admin SDK 서비스 계정 키 생성

1. Firebase Console > 프로젝트 설정 (⚙️ 아이콘)
2. "서비스 계정" 탭 선택
3. "새 비공개 키 생성" 클릭
4. JSON 파일 다운로드
5. 다운로드한 JSON 파일 이름을 `firebase-service-account.json`으로 변경
6. 프로젝트의 `src/main/resources/` 폴더에 복사

## 3. 웹 앱 추가 (웹 푸시용)

1. Firebase Console > 프로젝트 설정
2. "내 앱" 섹션에서 웹 앱 추가 (</>)
3. 앱 닉네임 입력
4. "Firebase Hosting 설정" 체크 해제
5. "앱 등록" 클릭
6. **Web Push 인증서 생성**:
   - 프로젝트 설정 > Cloud Messaging 탭
   - "웹 푸시 인증서" 섹션에서 "키 쌍 생성"
   - 생성된 키는 웹 클라이언트에서 사용

## 4. Android 앱 추가 (안드로이드 푸시용)

1. Firebase Console > 프로젝트 설정
2. "내 앱" 섹션에서 Android 앱 추가
3. Android 패키지 이름 입력 (예: `com.tunit.app`)
4. `google-services.json` 파일 다운로드
5. 안드로이드 프로젝트의 `app/` 폴더에 추가

## 5. iOS 앱 추가 (iOS 푸시용)

1. Firebase Console > 프로젝트 설정
2. "내 앱" 섹션에서 iOS 앱 추가
3. iOS 번들 ID 입력
4. `GoogleService-Info.plist` 파일 다운로드
5. Xcode 프로젝트에 추가
6. **APNs 인증 키 업로드**:
   - Apple Developer > Certificates, Identifiers & Profiles
   - Keys 섹션에서 새 키 생성 (APNs 체크)
   - `.p8` 파일 다운로드
   - Firebase Console > 프로젝트 설정 > Cloud Messaging
   - APNs 인증 키 업로드

## 6. application.yml 설정

```yaml
firebase:
  config:
    path: firebase-service-account.json  # 또는 절대 경로
```

## 7. .gitignore 추가

**중요: 민감한 정보가 포함되어 있으므로 반드시 .gitignore에 추가**

```
# Firebase
firebase-service-account.json
src/main/resources/firebase-service-account.json
```

## 8. 환경변수로 설정 (프로덕션 환경 권장)

### 방법 1: 환경변수로 JSON 문자열 전달
```yaml
firebase:
  config:
    credentials: ${FIREBASE_SERVICE_ACCOUNT_JSON}
```

```bash
export FIREBASE_SERVICE_ACCOUNT_JSON='{"type": "service_account", "project_id": "..."}'
```

### 방법 2: 클라우드 스토리지에서 다운로드
- AWS S3, Google Cloud Storage 등에 업로드
- 애플리케이션 시작 시 다운로드

## 9. 테스트

### 서버 실행
```bash
./gradlew bootRun
```

### 토큰 등록 API 테스트
```bash
curl -X POST http://localhost:8080/api/notifications/device-token \
  -H "Content-Type: application/json" \
  -d '{
    "fcmToken": "YOUR_FCM_TOKEN",
    "deviceType": "WEB",
    "deviceId": "test-device-001"
  }'
```

### 푸시 알림 전송 테스트
```bash
curl -X POST http://localhost:8080/api/notifications/send \
  -H "Content-Type: application/json" \
  -d '{
    "userNo": 1,
    "notificationType": "SYSTEM_NOTICE",
    "title": "테스트 알림",
    "message": "푸시 알림 테스트 메시지입니다."
  }'
```

## 10. 클라이언트 연동

### 웹 (React/Vue/Vanilla JS)

```javascript
// Firebase SDK 설치
npm install firebase

// firebase-config.js
import { initializeApp } from 'firebase/app';
import { getMessaging, getToken } from 'firebase/messaging';

const firebaseConfig = {
  apiKey: "YOUR_API_KEY",
  authDomain: "YOUR_AUTH_DOMAIN",
  projectId: "YOUR_PROJECT_ID",
  storageBucket: "YOUR_STORAGE_BUCKET",
  messagingSenderId: "YOUR_MESSAGING_SENDER_ID",
  appId: "YOUR_APP_ID"
};

const app = initializeApp(firebaseConfig);
const messaging = getMessaging(app);

// FCM 토큰 가져오기
export async function requestNotificationPermission() {
  try {
    const permission = await Notification.requestPermission();
    if (permission === 'granted') {
      const token = await getToken(messaging, {
        vapidKey: 'YOUR_WEB_PUSH_CERTIFICATE_KEY'
      });
      
      // 서버에 토큰 등록
      await fetch('/api/notifications/device-token', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          fcmToken: token,
          deviceType: 'WEB'
        })
      });
      
      return token;
    }
  } catch (error) {
    console.error('FCM 토큰 획득 실패:', error);
  }
}
```

### Android (Kotlin)

```kotlin
// build.gradle.kts
dependencies {
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-messaging-ktx")
}

// MyFirebaseMessagingService.kt
class MyFirebaseMessagingService : FirebaseMessagingService() {
    
    override fun onNewToken(token: String) {
        // 서버에 토큰 등록
        registerTokenToServer(token)
    }
    
    private fun registerTokenToServer(token: String) {
        val request = DeviceTokenRequest(
            fcmToken = token,
            deviceType = "ANDROID"
        )
        // API 호출
        apiService.registerDeviceToken(request)
    }
}
```

### iOS (Swift)

```swift
// AppDelegate.swift
import FirebaseCore
import FirebaseMessaging

@main
class AppDelegate: UIResponder, UIApplicationDelegate, MessagingDelegate {
    
    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        FirebaseApp.configure()
        Messaging.messaging().delegate = self
        
        // 푸시 알림 권한 요청
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound, .badge]) { granted, _ in
            guard granted else { return }
            DispatchQueue.main.async {
                application.registerForRemoteNotifications()
            }
        }
        
        return true
    }
    
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        guard let token = fcmToken else { return }
        
        // 서버에 토큰 등록
        registerTokenToServer(token: token)
    }
    
    func registerTokenToServer(token: String) {
        let params = [
            "fcmToken": token,
            "deviceType": "IOS"
        ]
        // API 호출
        APIService.shared.registerDeviceToken(params: params)
    }
}
```

## 11. 프로덕션 배포 체크리스트

- [ ] Firebase 서비스 계정 키를 환경변수 또는 Secret Manager에 저장
- [ ] .gitignore에 firebase-service-account.json 추가 확인
- [ ] Cloud Messaging API 활성화 확인
- [ ] 각 플랫폼별 앱 등록 완료
- [ ] APNs 인증서 업로드 (iOS)
- [ ] 웹 푸시 인증서 생성 (Web)
- [ ] 알림 권한 요청 UI 구현
- [ ] 푸시 알림 수신 테스트 완료

## 12. 트러블슈팅

### 문제: "Firebase 초기화 실패"
**해결**: firebase-service-account.json 파일 위치 확인

### 문제: "registration-token-not-registered"
**해결**: 토큰이 만료되었거나 앱을 삭제했을 때 발생. 자동으로 비활성화됨

### 문제: iOS에서 푸시 알림이 오지 않음
**해결**: APNs 인증서가 올바르게 업로드되었는지 확인

### 문제: 웹에서 푸시 알림 권한 요청이 안됨
**해결**: HTTPS 환경에서만 작동 (localhost는 예외)

## 참고 자료

- [Firebase 공식 문서](https://firebase.google.com/docs)
- [FCM HTTP v1 API](https://firebase.google.com/docs/cloud-messaging/http-server-ref)
- [Firebase Admin SDK](https://firebase.google.com/docs/admin/setup)

