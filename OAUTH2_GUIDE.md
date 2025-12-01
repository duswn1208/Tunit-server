# OAuth2 소셜 로그인 통합 가이드

## 🏗️ 아키텍처 설계

### 확장 가능한 구조 (Strategy + Factory Pattern)

```
OAuth2UserInfo (인터페이스)
    ├── NaverOAuth2UserInfo
    ├── KakaoOAuth2UserInfo
    ├── GoogleOAuth2UserInfo
    └── AppleOAuth2UserInfo

OAuth2UserInfoFactory
    └── 제공자별 구현체 자동 선택

CustomOAuth2UserService
    └── 모든 제공자 공통 로직 처리
```

## 📁 파일 구조

```
com/tunit/domain/user/
├── oauth2/
│   ├── OAuth2UserInfo.java           # 공통 인터페이스
│   ├── OAuth2UserInfoFactory.java     # 팩토리 클래스
│   ├── NaverOAuth2UserInfo.java       # 네이버 구현체
│   ├── KakaoOAuth2UserInfo.java       # 카카오 구현체
│   ├── GoogleOAuth2UserInfo.java      # 구글 구현체
│   └── AppleOAuth2UserInfo.java       # 애플 구현체
├── define/
│   └── UserProvider.java              # NAVER, KAKAO, GOOGLE, APPLE
├── entity/
│   └── UserMain.java                  # createOAuthUser() 메서드
└── service/
    └── CustomOAuth2UserService.java   # 통합 OAuth2 서비스
```

## 🔑 제공자별 Client ID/Secret 발급 방법

### 1️⃣ 네이버 (현재 설정 완료)
- URL: https://developers.naver.com/apps/
- Callback URL: `http://localhost:8080/login/oauth2/code/naver`

### 2️⃣ 카카오
1. https://developers.kakao.com/ 접속
2. 내 애플리케이션 > 애플리케이션 추가하기
3. **REST API 키** → `client-id`에 입력
4. 보안 > Client Secret 발급 → `client-secret`에 입력
5. 플랫폼 설정 > Web > Redirect URI 등록
   - `http://localhost:8080/login/oauth2/code/kakao`
   - `http://localhost:5173` (프론트엔드)
6. 동의 항목 설정
   - 프로필 정보(닉네임/프로필 이미지): 선택 동의
   - 카카오계정(이메일): 선택 동의

### 3️⃣ 구글
1. https://console.cloud.google.com/ 접속
2. 프로젝트 생성
3. API 및 서비스 > OAuth 동의 화면
   - 외부 선택 (테스트 사용자 추가 가능)
4. 사용자 인증 정보 > OAuth 2.0 클라이언트 ID 만들기
   - 애플리케이션 유형: 웹 애플리케이션
   - 승인된 리디렉션 URI: `http://localhost:8080/login/oauth2/code/google`
5. 생성된 **클라이언트 ID**와 **클라이언트 보안 비밀** 복사

### 4️⃣ 애플 (Sign in with Apple)
1. https://developer.apple.com/account/ 접속
2. Certificates, Identifiers & Profiles
3. Identifiers > App IDs 생성
4. Services IDs 생성
   - Sign In with Apple 활성화
   - Return URLs: `http://localhost:8080/login/oauth2/code/apple`
5. Keys 생성
   - Sign in with Apple 활성화
   - Private Key 다운로드 (.p8 파일)
6. **Client Secret은 JWT로 생성** (복잡함, 별도 라이브러리 필요)

⚠️ **애플은 설정이 복잡하므로 후순위 추천**

## 🔧 application.yml 설정

발급받은 키를 `YOUR_*` 부분에 입력:

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          kakao:
            client-id: YOUR_KAKAO_CLIENT_ID
            client-secret: YOUR_KAKAO_CLIENT_SECRET
          google:
            client-id: YOUR_GOOGLE_CLIENT_ID
            client-secret: YOUR_GOOGLE_CLIENT_SECRET
```

## 🚀 사용 방법

### 새로운 OAuth2 제공자 추가 시
1. `UserProvider` enum에 제공자 추가
2. `OAuth2UserInfo` 구현체 생성 (예: `NaverOAuth2UserInfo.java` 참고)
3. `OAuth2UserInfoFactory`에 switch 케이스 추가
4. `application.yml`에 설정 추가

### 프론트엔드 로그인 버튼 URL
```javascript
// 네이버
<a href="http://localhost:8080/oauth2/authorization/naver">네이버 로그인</a>

// 카카오
<a href="http://localhost:8080/oauth2/authorization/kakao">카카오 로그인</a>

// 구글
<a href="http://localhost:8080/oauth2/authorization/google">구글 로그인</a>

// 애플
<a href="http://localhost:8080/oauth2/authorization/apple">애플 로그인</a>
```

## 📊 제공자별 제공 정보

| 제공자 | ID | 이름 | 이메일 | 전화번호 | 프로필 이미지 |
|--------|----|----|--------|----------|--------------|
| 네이버 | ✅ | ✅ | ✅ | ✅ | ✅ |
| 카카오 | ✅ | ✅ | ✅ | ✅(선택) | ✅ |
| 구글   | ✅ | ✅ | ✅ | ❌ | ✅ |
| 애플   | ✅ | ✅(최초만) | ✅ | ❌ | ❌ |

## 🔍 로그 확인

OAuth2 로그인 시 다음과 같은 로그가 출력됩니다:

```
OAuth2 로그인 시도: provider=kakao
OAuth2 사용자 정보 추출 완료: provider=KAKAO, providerId=123456, name=홍길동, email=test@kakao.com
신규 OAuth2 사용자 생성: provider=KAKAO, providerId=123456
세션 저장 완료: userNo=1, role=null
```

## 🐛 PostgreSQL 에러 해결

"prepared statement S_1 already exists" 에러는 HikariCP 커넥션 풀 설정 문제입니다.

**해결책 (이미 적용됨):**
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 5
      minimum-idle: 1
      connection-timeout: 30000
      idle-timeout: 600000      # 10분
      max-lifetime: 1800000     # 30분
```

추가 설정:
```yaml
spring:
  jpa:
    properties:
      hibernate:
        jdbc:
          lob:
            non_contextual_creation: true
```

## 💡 장점

### 1. **확장성**
- 새로운 제공자 추가 시 기존 코드 수정 최소화
- 인터페이스 기반 설계로 일관된 처리

### 2. **유지보수성**
- 제공자별 로직이 분리되어 있어 수정 용이
- 공통 로직은 `CustomOAuth2UserService`에서 한 번만 관리

### 3. **테스트 용이성**
- Mock 객체 생성이 쉬움
- 제공자별 단위 테스트 가능

### 4. **코드 품질**
- 중복 코드 제거
- Single Responsibility Principle 준수
- Open-Closed Principle 준수

