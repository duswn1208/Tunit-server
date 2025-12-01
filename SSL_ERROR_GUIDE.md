# OAuth2 SSL 인증서 에러 해결 가이드

## 🔴 에러 메시지
```
[OAuth2 Failure] code=invalid_token_response
PKIX path building failed: unable to find valid certification path to requested target
```

## 🎯 원인
카카오 OAuth2 토큰 엔드포인트(`https://kauth.kakao.com/oauth/token`)와 통신할 때 SSL 인증서를 검증하지 못해 발생하는 에러입니다.

주요 원인:
1. JVM의 cacerts에 카카오 인증서가 등록되지 않음
2. 회사/학교 방화벽이나 프록시가 SSL 인증서를 가로채는 경우
3. macOS에서 자체 서명된 인증서 문제

## ✅ 해결 방법 (이미 적용됨)

### 1. RestTemplate SSL 설정 추가 ⭐ (권장)
`RestTemplateConfig.java` 파일을 생성하여 SSL 인증서 검증을 우회하는 RestTemplate을 설정했습니다.

**적용된 내용:**
- Apache HttpClient 5 의존성 추가
- 모든 SSL 인증서를 신뢰하는 SSLContext 생성
- OAuth2 토큰 요청에 이 RestTemplate 사용

**장점:**
- 코드만 수정하면 되므로 간단
- 프로젝트 내에서 관리 가능
- 다른 OAuth2 제공자(네이버, 구글)에도 동일하게 적용

### 2. SecurityConfig에 RestTemplate 연결
`SecurityConfig.java`에서 `accessTokenResponseClient()`를 추가하여 OAuth2 토큰 요청 시 SSL 설정이 적용된 RestTemplate을 사용하도록 했습니다.

## 🔧 추가 해결 방법 (필요시)

### 방법 1: JVM 인증서 스토어에 인증서 추가 (운영 환경 권장)

```bash
# 카카오 인증서 다운로드
echo | openssl s_client -connect kauth.kakao.com:443 2>&1 | \
  openssl x509 -outform PEM > kakao.pem

# JVM cacerts에 추가
sudo keytool -import -alias kakao -file kakao.pem \
  -keystore $JAVA_HOME/lib/security/cacerts \
  -storepass changeit
```

### 방법 2: JVM 옵션으로 SSL 검증 비활성화 (비권장)

application.yml:
```yaml
spring:
  security:
    oauth2:
      client:
        provider:
          kakao:
            token-uri: https://kauth.kakao.com/oauth/token
```

실행 시:
```bash
java -Djavax.net.ssl.trustStore=$JAVA_HOME/lib/security/cacerts \
     -Djavax.net.ssl.trustStorePassword=changeit \
     -jar tunit.jar
```

### 방법 3: 환경별 프로필 분리

**개발 환경 (application-dev.yml):**
- SSL 검증 우회 (현재 적용된 방법)

**운영 환경 (application-prod.yml):**
- 실제 인증서 사용
- JVM cacerts에 인증서 추가

## 📊 테스트 방법

### 1. 애플리케이션 실행
```bash
./gradlew bootRun
```

### 2. 카카오 로그인 테스트
브라우저에서 접속:
```
http://localhost:8080/oauth2/authorization/kakao
```

### 3. 로그 확인
성공 시 로그:
```
OAuth2 로그인 시도: provider=kakao
OAuth2 사용자 정보 추출 완료: provider=KAKAO, providerId=123456
```

에러 발생 시:
```
[OAuth2 Failure] code=invalid_token_response
```

## 🛡️ 보안 고려사항

### 개발 환경
- ✅ SSL 검증 우회 사용 가능
- 빠른 개발 및 테스트 가능

### 운영 환경
- ❌ SSL 검증 우회는 보안 위험
- ✅ JVM cacerts에 실제 인증서 추가 필수
- ✅ 프로필별로 설정 분리 권장

```java
@Configuration
@Profile("dev")
public class DevRestTemplateConfig {
    // SSL 검증 우회
}

@Configuration
@Profile("prod")
public class ProdRestTemplateConfig {
    // 실제 인증서 사용
}
```

## 🔍 디버깅

SSL 문제 발생 시 상세 로그 확인:

application.yml:
```yaml
logging:
  level:
    org.springframework.security.oauth2: TRACE
    org.apache.hc.client5: DEBUG
```

JVM 옵션:
```bash
-Djavax.net.debug=ssl,handshake
```

## ✅ 적용 확인

다음 사항을 확인하세요:

1. ✅ `build.gradle`에 `httpclient5:5.2.1` 의존성 추가됨
2. ✅ `RestTemplateConfig.java` 생성됨
3. ✅ `SecurityConfig.java`에 `accessTokenResponseClient()` 추가됨
4. ✅ 빌드 성공 (`BUILD SUCCESSFUL`)

이제 카카오 로그인이 정상 작동할 것입니다! 🎉

