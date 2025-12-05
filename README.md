# 🎓 Tunit - 과외 매칭 플랫폼

> 학생과 튜터를 연결하는 과외 관리 및 매칭 서비스 백엔드 시스템

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Latest-blue.svg)](https://www.postgresql.org/)
[![Gradle](https://img.shields.io/badge/Gradle-Latest-02303A.svg)](https://gradle.org/)

## 📋 목차

- [프로젝트 소개](#-프로젝트-소개)
- [주요 기능](#-주요-기능)
- [기술 스택](#-기술-스택)
- [아키텍처](#-아키텍처)
- [도메인 설계](#-도메인-설계)
- [주요 구현 사항](#-주요-구현-사항)
- [프로젝트 구조](#-프로젝트-구조)
- [실행 방법](#-실행-방법)

---

## 🎯 프로젝트 소개

**Tunit**는 학생과 튜터를 연결하고 과외 일정을 효율적으로 관리하는 과외 매칭 플랫폼의 백엔드 서버입니다.
실시간 알림, 결제 관리, 일정 예약, 리뷰 시스템 등 과외 서비스에 필요한 핵심 기능을 제공합니다.
기존 예약 관리 플랫폼과는 다르게 1:N 매칭뿐만 아니라 1:1 매칭도 지원하며 
정기적으로 동일한 요일/시간을 자동 등록해주는 시스템과 선착순으로 회원이 수업을 예약할 수 있는 기능도 포함하고 있습니다.

### 💡 프로젝트 목표

- **효율적인 매칭**: 학생의 요구사항과 튜터의 프로필을 기반으로 최적의 매칭 제공
- **편리한 일정 관리**: 고정/단발 수업 예약, 일정 변경, 취소 기능
- **실시간 소통**: Firebase FCM을 활용한 푸시 알림으로 실시간 상태 업데이트
- **안전한 거래**: 계약 체결, 결제, 환불 등 전체 거래 프로세스 관리

---

## ✨ 주요 기능

### 1️⃣ 사용자 관리
- 소셜 로그인 (Naver, Kakao, Google, Apple OAuth2)
- 학생/튜터 역할 기반 프로필 관리
- 세션 기반 인증 및 권한 관리

### 2️⃣ 과외 매칭 & 계약
- 튜터 프로필 등록 (전문 분야, 경력, 자격증 등)
- 학생-튜터 매칭 및 계약 체결
- 계약 취소 및 환불 처리

### 3️⃣ 수업 예약 시스템
- **고정 수업**: 정기적인 수업 일정 자동 예약 / 선착순 예약 기능
- **단발 수업**: 일회성 수업 예약
- 수업 확정/취소/변경 관리
- 참여하지 않은 수업에 관리 자동 노쇼 처리
- 튜터 가능 시간대 및 휴일 관리

### 4️⃣ 알림 시스템
- Firebase Cloud Messaging(FCM) 푸시 알림
- 이벤트 기반 자동 알림 발송
  - 수업 확정/취소
  - 결제 완료
  - 계약 체결
  - 리뷰 요청
- AOP 기반 선언적 알림 처리
- 트랜잭션 커밋 후 알림 전송 보장

### 5️⃣ 리뷰 & 평가
- 수업 후 학생 리뷰 작성
- 튜터 평점 관리

### 6️⃣ 배치 처리
- Spring Batch를 활용한 정기 작업 자동화
- 수업 예약 자동 생성 및 관리

---

## 🛠 기술 스택

### Backend Framework
- **Spring Boot 3.5.4** - 최신 버전의 Spring 프레임워크
- **Java 17** - LTS 버전 Java 사용
- **Gradle** - 빌드 및 의존성 관리

### Database & ORM
- **PostgreSQL** - 관계형 데이터베이스 (Supabase 호스팅)
- **Spring Data JPA** - ORM 및 데이터 접근 계층
- **Hibernate** - JPA 구현체

### Security & Authentication
- **Spring Security** - 인증/인가 프레임워크
- **OAuth 2.0 Client** - 소셜 로그인 통합
  - Naver, Kakao, Google, Apple(지원 예정) 지원
- **Session 기반 인증** - 세션 관리 및 인증 상태 유지

### Infrastructure & DevOps
- **Firebase Cloud Messaging** - 푸시 알림 서비스
- **Spring Batch** - 배치 작업 처리
- **Spring AOP** - 관점 지향 프로그래밍 (알림, 로깅)

### Development Tools
- **Lombok** - 보일러플레이트 코드 감소
- **Spring Boot DevTools** - 개발 생산성 향상
- **Apache POI** - Excel 파일 처리

### Testing
- **JUnit 5** - 단위 테스트
- **Spring Boot Test** - 통합 테스트
- **Spring Security Test** - 보안 관련 테스트
- **Spring Batch Test** - 배치 작업 테스트

---

## 🏗 아키텍처

### 계층형 아키텍처 (Layered Architecture)

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│  (Controller, Handler, Resolver)        │
├─────────────────────────────────────────┤
│          Application Layer              │
│      (Service, Event, Aspect)           │
├─────────────────────────────────────────┤
│           Domain Layer                  │
│    (Entity, Repository, DTO)            │
├─────────────────────────────────────────┤
│       Infrastructure Layer              │
│  (Config, Security, Batch, Common)      │
└─────────────────────────────────────────┘
```

### 주요 설계 패턴

#### 1. **Domain-Driven Design (DDD)**
- 도메인 중심의 패키지 구조
- 각 도메인별 독립적인 비즈니스 로직 관리
- 명확한 도메인 경계 설정

#### 2. **Event-Driven Architecture**
- Spring의 `ApplicationEvent`를 활용한 이벤트 기반 처리
- 도메인 간 느슨한 결합 (Loose Coupling)
- 알림 발송, 로깅 등 부가 기능의 비동기 처리

#### 3. **Aspect-Oriented Programming (AOP)**
- 횡단 관심사(Cross-Cutting Concerns) 분리
- `@SendNotification` 커스텀 어노테이션으로 선언적 알림 처리
- 트랜잭션 커밋 후 알림 전송 보장

#### 4. **Repository Pattern**
- Spring Data JPA를 통한 데이터 접근 추상화
- 복잡한 쿼리는 JPQL/QueryDSL 활용

---

## 📦 도메인 설계

프로젝트는 **8개의 핵심 도메인**으로 구성되어 있으며, 각 도메인은 독립적인 비즈니스 로직을 담당합니다.

### 1. 👤 User Domain (사용자)
**역할**: 사용자 인증 및 계정 관리

```
domain/user/
├── controller/     # REST API 엔드포인트
├── entity/        # UserMain (사용자 엔티티)
├── service/       # 사용자 관리 비즈니스 로직
├── repository/    # 데이터 접근 계층
├── dto/          # 요청/응답 DTO
├── oauth2/       # OAuth2 인증 처리
├── define/       # Enum (UserRole, UserStatus, UserProvider)
└── exception/    # 사용자 관련 예외
```

**핵심 기능**:
- OAuth2 소셜 로그인 (Naver, Kakao, Google, Apple)
- 사용자 역할 관리 (STUDENT, TUTOR)
- 사용자 상태 관리 (ACTIVE, WAITING, INACTIVE)

---

### 2. 👨‍🏫 Tutor Domain (튜터)
**역할**: 튜터 프로필 및 가용 시간 관리

```
domain/tutor/
├── controller/    # 튜터 관련 API
├── entity/       # TutorProfile, TutorAvailableTime, TutorHoliday
├── service/      # 튜터 프로필, 가용시간, 휴일 관리
├── repository/   # 데이터 접근
├── dto/         # 튜터 정보 DTO
├── define/      # 튜터 관련 Enum
└── exception/   # 튜터 관련 예외
```

**핵심 기능**:
- 튜터 프로필 등록 및 수정
- 가능한 수업 시간대 관리
- 휴일 등록 및 관리
- 튜터 검색 및 필터링

---

### 3. 👨‍🎓 Student Domain (학생)
**역할**: 학생 정보 관리

```
domain/student/
├── controller/   # 학생 관련 API
├── entity/      # StudentInfo
├── service/     # 학생 정보 관리
├── repository/  # 데이터 접근
└── dto/        # 학생 정보 DTO
```

**핵심 기능**:
- 학생 프로필 등록 및 수정
- 학습 이력 관리

---

### 4. 📝 Contract Domain (계약)
**역할**: 학생-튜터 간 계약 관리

```
domain/contract/
├── controller/    # 계약 관련 API
├── entity/       # StudentTutorContract, ContractCancel
├── service/      # 계약 체결, 취소, 결제 처리
├── repository/   # 데이터 접근
├── dto/         # 계약 정보 DTO
├── define/      # 계약 상태 Enum
└── exception/   # 계약 관련 예외
```

**핵심 기능**:
- 계약 체결 및 취소
- 계약 상태 관리 (대기, 진행중, 완료, 취소)
- 결제 정보 연동

---

### 5. 📅 Lesson Domain (수업)
**역할**: 수업 예약 및 일정 관리

```
domain/lesson/
├── controller/     # 수업 예약 API
├── entity/        # LessonReservation, FixedLessonReservation
├── service/       # 수업 예약, 확정, 취소 처리
├── repository/    # 데이터 접근
├── dto/          # 수업 정보 DTO
├── feedback/     # 수업 피드백 (예정)
├── validate/     # 예약 검증 로직
├── define/       # 수업 상태 Enum
└── exception/    # 수업 관련 예외
```

**핵심 기능**:
- **고정 수업 예약**: 정기적인 수업 일정 자동 생성
- **단발 수업 예약**: 일회성 수업 예약
- 수업 확정/취소
- 수업 상태 관리 (예약, 확정, 완료, 취소)
- Excel 파일을 통한 대량 예약

---

### 6. ⭐ Review Domain (리뷰)
**역할**: 수업 후 리뷰 및 평가 관리

```
domain/review/
├── controller/   # 리뷰 API
├── entity/      # LessonReview
├── service/     # 리뷰 작성, 조회
├── repository/  # 데이터 접근
├── dto/        # 리뷰 정보 DTO
└── exception/  # 리뷰 관련 예외
```

**핵심 기능**:
- 수업 후 리뷰 작성
- 튜터 평점 계산
- 리뷰 조회 및 관리

---

### 7. 🔔 Notification Domain (알림)
**역할**: 푸시 알림 및 이벤트 기반 알림 처리

```
domain/notification/
├── controller/    # 알림 설정 API
├── entity/       # NotifyPush, UserDeviceToken
├── service/      # 푸시 알림 전송, 디바이스 토큰 관리
├── repository/   # 데이터 접근
├── event/        # 도메인 이벤트 및 리스너
│   ├── NotificationEvent.java
│   ├── LessonConfirmedEvent.java
│   ├── LessonCancelledEvent.java
│   ├── ContractSignedEvent.java
│   ├── PaymentCompletedEvent.java
│   └── NotificationEventListener.java
├── aspect/       # AOP 기반 알림 처리
│   └── SendNotificationAspect.java
├── annotation/   # 커스텀 어노테이션
│   └── @SendNotification
├── scheduler/    # 스케줄링 작업
├── dto/         # 알림 DTO
└── define/      # 알림 타입 Enum
```

**핵심 기능**:
- Firebase Cloud Messaging (FCM) 푸시 알림
- 이벤트 기반 자동 알림
- AOP를 통한 선언적 알림 처리
- 멀티 디바이스 토큰 관리
- 알림 전송 이력 관리

**이벤트 종류**:
- `LessonConfirmedEvent`: 수업 확정 시 알림
- `LessonCancelledEvent`: 수업 취소 시 알림
- `ContractSignedEvent`: 계약 체결 시 알림
- `PaymentCompletedEvent`: 결제 완료 시 알림
- `ReviewRequestEvent`: 리뷰 요청 시 알림

---

### 8. 📍 Region Domain (지역)
**역할**: 지역 정보 관리 (시/도, 시/군/구)

```
domain/region/
├── controller/   # 지역 정보 API
├── service/     # 지역 데이터 초기화 및 조회
├── dto/        # 지역 정보 DTO
└── util/       # 지역 데이터 파싱
```

**핵심 기능**:
- 전국 시/도, 시/군/구 정보 제공
- 튜터 활동 지역 설정

---

## 💎 주요 구현 사항

### 1. OAuth2 소셜 로그인 통합
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain apiChain(HttpSecurity http) throws Exception {
        http.oauth2Login(oauth -> oauth
            .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
            .successHandler(customOAuth2SuccessHandler)
            .failureHandler(customOAuth2FailureHandler));
        return http.build();
    }
}
```

**지원 제공자**: Naver, Kakao, Google, Apple

---

### 2. 이벤트 기반 알림 시스템

#### 이벤트 발행
```java
@Service
public class LessonService {
    private final ApplicationEventPublisher eventPublisher;
    
    public void confirmLesson(Long lessonId) {
        // 수업 확정 로직
        LessonReservation lesson = lessonRepository.findById(lessonId);
        lesson.confirm();
        
        // 이벤트 발행
        eventPublisher.publishEvent(new LessonConfirmedEvent(
            lesson.getStudentId(), 
            lesson.getLessonDate()
        ));
    }
}
```

#### 이벤트 리스너
```java
@Component
@EventListener
public class NotificationEventListener {
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleLessonConfirmed(LessonConfirmedEvent event) {
        pushNotificationService.sendLessonConfirmedNotification(
            event.getStudentId(), 
            event.getLessonDate()
        );
    }
}
```

---

### 3. AOP 기반 선언적 알림 처리

#### 커스텀 어노테이션
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SendNotification {
    String userNoSpEL();
    String title();
    String body();
}
```

#### 사용 예시
```java
@Service
public class ContractService {
    @SendNotification(
        userNoSpEL = "#result.studentId",
        title = "계약 체결 완료",
        body = "튜터와의 계약이 성공적으로 체결되었습니다."
    )
    public ContractResponseDto createContract(ContractRequestDto dto) {
        // 계약 생성 로직
        return responseDto;
    }
}
```

#### AOP Aspect
```java
@Aspect
@Component
public class SendNotificationAspect {
    @AfterReturning(pointcut = "@annotation(SendNotification)", returning = "result")
    public void sendNotification(JoinPoint joinPoint, Object result) {
        // SpEL을 사용하여 동적으로 사용자 식별
        // 트랜잭션 커밋 후 알림 전송
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        pushNotificationService.send(notification);
                    }
                }
            );
        }
    }
}
```

**장점**:
- 비즈니스 로직과 알림 로직 분리
- 선언적 프로그래밍으로 코드 가독성 향상
- 트랜잭션 커밋 성공 시에만 알림 전송 보장

---

### 4. Spring Batch를 활용한 자동화

```java
@Configuration
public class BatchConfig {
    // 고정 수업 자동 예약 배치 작업
    // 매주 월요일 자동 실행
}
```

---

### 5. 커스텀 세션 관리

#### 커스텀 어노테이션
```java
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginUser {
}
```

#### ArgumentResolver
```java
@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public Object resolveArgument(MethodParameter parameter, ...) {
        return sessionUser; // 세션에서 사용자 정보 추출
    }
}
```

#### 사용 예시
```java
@RestController
public class UserController {
    @GetMapping("/profile")
    public UserProfileDto getProfile(@LoginUser SessionUser user) {
        return userService.getProfile(user.getUserNo());
    }
}
```

---

### 6. Excel 파일 처리

Apache POI를 활용한 대량 수업 예약 기능
```java
@Service
public class ExcelLessonReservationParser {
    public List<LessonReservationDto> parseExcelFile(MultipartFile file) {
        // Excel 파일 파�ing 및 DTO 변환
    }
}
```

---

## 📂 프로젝트 구조

```
tunit/
├── src/
│   ├── main/
│   │   ├── java/com/tunit/
│   │   │   ├── TunitApplication.java          # 메인 애플리케이션
│   │   │   │
│   │   │   ├── domain/                        # 도메인 계층
│   │   │   │   ├── user/                     # 사용자 도메인
│   │   │   │   │   ├── controller/
│   │   │   │   │   ├── service/
│   │   │   │   │   ├── repository/
│   │   │   │   │   ├── entity/
│   │   │   │   │   ├── dto/
│   │   │   │   │   ├── oauth2/
│   │   │   │   │   ├── define/
│   │   │   │   │   └── exception/
│   │   │   │   │
│   │   │   │   ├── tutor/                    # 튜터 도메인
│   │   │   │   ├── student/                  # 학생 도메인
│   │   │   │   ├── contract/                 # 계약 도메인
│   │   │   │   ├── lesson/                   # 수업 도메인
│   │   │   │   ├── review/                   # 리뷰 도메인
│   │   │   │   ├── notification/             # 알림 도메인
│   │   │   │   │   ├── event/               # 이벤트 처리
│   │   │   │   │   ├── aspect/              # AOP 알림
│   │   │   │   │   ├── annotation/          # 커스텀 어노테이션
│   │   │   │   │   └── scheduler/           # 스케줄러
│   │   │   │   └── region/                   # 지역 도메인
│   │   │   │
│   │   │   ├── common/                        # 공통 모듈
│   │   │   │   ├── config/                   # 설정 클래스
│   │   │   │   │   ├── SecurityConfig.java
│   │   │   │   │   ├── FirebaseConfig.java
│   │   │   │   │   ├── AsyncConfig.java
│   │   │   │   │   └── WebConfig.java
│   │   │   │   ├── handler/                  # 전역 핸들러
│   │   │   │   │   ├── GlobalApiExceptionHandler.java
│   │   │   │   │   ├── CustomOAuth2SuccessHandler.java
│   │   │   │   │   └── CustomOAuth2FailureHandler.java
│   │   │   │   ├── session/                  # 세션 관리
│   │   │   │   │   ├── annotation/
│   │   │   │   │   ├── service/
│   │   │   │   │   └── dto/
│   │   │   │   ├── util/                     # 유틸리티
│   │   │   │   └── dto/                      # 공통 DTO
│   │   │   │
│   │   │   └── batch/                         # 배치 처리
│   │   │       └── config/
│   │   │
│   │   └── resources/
│   │       ├── application.yml               # 애플리케이션 설정
│   │       └── application-prod.yml          # 프로덕션 설정
│   │
│   └── test/                                  # 테스트 코드
│       └── java/com/tunit/
│           ├── domain/
│           │   ├── contract/
│           │   ├── notification/
│           │   ├── lesson/
│           │   └── tutor/
│           └── TunitApplicationTests.java
│
├── build.gradle                               # Gradle 빌드 설정
├── settings.gradle
├── gradlew
├── gradlew.bat
│
├── README.md                                  # 프로젝트 문서
├── OAUTH2_GUIDE.md                           # OAuth2 설정 가이드
├── FCM_SETUP_GUIDE.md                        # FCM 설정 가이드
├── NOTIFICATION_USAGE_GUIDE.md               # 알림 사용 가이드
└── SSL_ERROR_GUIDE.md                        # SSL 오류 해결 가이드
```

### 패키지 구조 설명

#### 🎯 Domain Layer
각 도메인은 다음과 같은 표준 구조를 따릅니다:
- **controller**: REST API 엔드포인트
- **service**: 비즈니스 로직
- **repository**: 데이터 접근 (Spring Data JPA)
- **entity**: JPA 엔티티
- **dto**: 데이터 전송 객체
- **define**: Enum 정의
- **exception**: 도메인별 예외

#### 🔧 Common Layer
- **config**: 전역 설정 (Security, Firebase, Async, CORS)
- **handler**: 전역 예외 처리, OAuth2 핸들러
- **session**: 세션 관리 및 커스텀 어노테이션
- **util**: 공통 유틸리티 (Excel 파싱, 페이징 등)

#### ⚙️ Batch Layer
- Spring Batch 작업 설정
- 스케줄링 작업

---

## 🚀 실행 방법

### Prerequisites
- **Java 17** 이상
- **PostgreSQL** (또는 Supabase)
- **Firebase 프로젝트** (FCM 사용 시)
- **OAuth2 클라이언트 ID/Secret** (Naver, Kakao, Google, Apple)

### 1. 저장소 클론
```bash
git clone https://github.com/your-repo/tunit.git
cd tunit
```

### 2. 환경 변수 설정

`src/main/resources/application.yml` 파일을 생성하고 다음 내용을 설정합니다:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://your-db-host:5432/tunit
    username: your-username
    password: your-password
    driver-class-name: org.postgresql.Driver

  security:
    oauth2:
      client:
        registration:
          naver:
            client-id: your-naver-client-id
            client-secret: your-naver-client-secret
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
            authorization-grant-type: authorization_code
            scope: name, email, mobile
          kakao:
            client-id: your-kakao-client-id
            client-secret: your-kakao-client-secret
            # ... (기타 OAuth2 설정)

firebase:
  config:
    credentials: ${FIREBASE_SERVICE_ACCOUNT_JSON}

service-url:
  web: http://localhost:5173
  server: http://localhost:8080
```

### 3. Firebase 설정
1. Firebase Console에서 서비스 계정 키 JSON 다운로드
2. 환경 변수로 설정:
```bash
export FIREBASE_SERVICE_ACCOUNT_JSON='{"type":"service_account",...}'
```

### 4. 빌드 및 실행
```bash
# Gradle 빌드
./gradlew clean build

# 애플리케이션 실행
./gradlew bootRun
```

### 5. 테스트 실행
```bash
# 전체 테스트
./gradlew test

# 특정 테스트
./gradlew test --tests "com.tunit.domain.notification.*"
```

### 6. API 접속
애플리케이션이 실행되면 다음 URL로 접속:
- **API Server**: http://localhost:8080
- **Health Check**: http://localhost:8080/actuator/health (설정 시)

---

## 📊 데이터베이스 스키마

### 주요 테이블
- `user_main`: 사용자 정보
- `tutor_profile`: 튜터 프로필
- `student_info`: 학생 정보
- `student_tutor_contract`: 학생-튜터 계약
- `lesson_reservation`: 수업 예약
- `fixed_lesson_reservation`: 고정 수업 예약
- `lesson_review`: 수업 리뷰
- `notify_push`: 푸시 알림 이력
- `user_device_token`: 디바이스 토큰

---

## 🧪 테스트 커버리지

프로젝트는 다음과 같은 테스트를 포함합니다:
- **단위 테스트**: 각 서비스 로직 검증
- **통합 테스트**: API 엔드포인트 및 데이터베이스 통합 테스트
- **이벤트 테스트**: 이벤트 발행 및 리스너 동작 검증

주요 테스트 케이스:
```
✅ ContractServiceIntegrationTest
✅ LessonServiceTest
✅ NotificationServiceTest
✅ PushNotificationServiceTest
✅ NotificationEventListenerTest
✅ TutorProfileServiceTest
✅ TutorAvailableTimeServiceTest
```

---

## 📖 추가 문서

프로젝트 루트에 있는 상세 가이드 문서:

- **[OAuth2 설정 가이드](./OAUTH2_GUIDE.md)**: 소셜 로그인 설정 방법
- **[FCM 설정 가이드](./FCM_SETUP_GUIDE.md)**: Firebase Cloud Messaging 설정
- **[알림 사용 가이드](./NOTIFICATION_USAGE_GUIDE.md)**: 알림 시스템 사용법
- **[SSL 오류 해결](./SSL_ERROR_GUIDE.md)**: SSL 관련 오류 해결 방법

---

## 🎓 학습 포인트

이 프로젝트를 통해 다음 역량을 확인할 수 있습니다:

### 1. **백엔드 설계 능력**
- 계층형 아키텍처 구현
- Domain-Driven Design (DDD) 적용
- RESTful API 설계

### 2. **Spring 생태계 활용**
- Spring Boot 3.x 최신 기능 활용
- Spring Security를 활용한 인증/인가
- Spring Data JPA를 통한 효율적인 데이터 접근
- Spring Batch를 활용한 배치 처리
- Spring AOP를 활용한 횡단 관심사 분리

### 3. **이벤트 기반 아키텍처**
- 이벤트 발행/구독 패턴 구현
- 도메인 간 느슨한 결합
- 비동기 처리 (`@Async`, `@TransactionalEventListener`)

### 4. **외부 서비스 연동**
- OAuth2를 활용한 소셜 로그인 (4개 제공자)
- Firebase Cloud Messaging 푸시 알림
- PostgreSQL 데이터베이스 연동

### 5. **코드 품질 관리**
- Lombok을 통한 보일러플레이트 감소
- 커스텀 어노테이션 및 AOP 활용
- 명확한 패키지 구조 및 명명 규칙
- 단위/통합 테스트 작성

### 6. **비즈니스 로직 구현**
- 복잡한 예약 시스템 구현 (고정/단발 수업)
- 계약 및 결제 프로세스 관리
- 실시간 알림 시스템
- 리뷰 및 평점 시스템

---

## 🔧 개선 계획

- [ ] API 문서 자동화 (Swagger/SpringDoc)
- [ ] Redis를 활용한 캐싱
- [ ] QueryDSL을 활용한 복잡한 쿼리 최적화
- [ ] JWT 기반 인증으로 확장 (모바일 앱 지원)
- [ ] CI/CD 파이프라인 구축 (GitHub Actions)
- [ ] Docker 컨테이너화
- [ ] 성능 모니터링 (Prometheus, Grafana)

---

## 📧 문의

프로젝트에 대한 문의사항이 있으시면 언제든지 연락해주세요.

---
