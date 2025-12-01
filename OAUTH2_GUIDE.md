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


