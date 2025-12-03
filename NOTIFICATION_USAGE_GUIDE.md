# 푸시 알림 사용 가이드 (이벤트 & 어노테이션 방식)

## 🎯 3가지 방식 비교

### ❌ 방식 1: 직접 서비스 호출 (비추천)
```java
@Service
public class LessonService {
    private final NotificationEventService notificationEventService;
    
    public void confirmLesson(Long lessonNo) {
        Lesson lesson = lessonRepository.findById(lessonNo);
        lesson.confirm();
        
        // 알림 전송 코드가 비즈니스 로직에 섞임
        notificationEventService.sendLessonConfirmedNotification(...);
    }
}
```

### ✅ 방식 2: 이벤트 기반 (추천)
```java
@Service
public class LessonService {
    private final ApplicationEventPublisher eventPublisher;
    
    public void confirmLesson(Long lessonNo) {
        Lesson lesson = lessonRepository.findById(lessonNo);
        lesson.confirm();
        
        // 이벤트 발행 (한 줄!)
        eventPublisher.publishEvent(
            new LessonConfirmedEvent(this, lesson.getStudentNo(), lesson.getId(), 
                                   lesson.getTitle(), lesson.getStartTime())
        );
    }
}
```

### ✅ 방식 3: 어노테이션 기반 (가장 추천!)
```java
@Service
public class LessonService {
    
    @SendNotification(
        type = NotificationType.LESSON_CONFIRMED,
        title = "수업이 확정되었습니다",
        message = "#{#result.title} 수업이 #{#result.startTime}에 확정되었습니다",
        userNoField = "#result.studentNo",
        deepLink = "/lessons/#{#result.id}"
    )
    public Lesson confirmLesson(Long lessonNo) {
        Lesson lesson = lessonRepository.findById(lessonNo);
        lesson.confirm();
        return lesson;  // 반환 값으로 알림 전송
    }
}
```

---

## 📋 방식 2: 이벤트 기반 상세 가이드

### 사용 가능한 이벤트

| 이벤트 클래스 | 용도 | 생성자 파라미터 |
|--------------|------|----------------|
| `LessonConfirmedEvent` | 수업 확정 | source, userNo, lessonNo, title, date |
| `LessonCancelledEvent` | 수업 취소 | source, userNo, title, reason |
| `PaymentCompletedEvent` | 결제 완료 | source, userNo, itemName, amount |
| `ContractSignedEvent` | 계약 체결 | source, tutorNo, studentNo, title |
| `ReviewRequestEvent` | 리뷰 요청 | source, userNo, lessonTitle |

### 이벤트 발행 방법

```java
@Service
@RequiredArgsConstructor
public class YourService {
    private final ApplicationEventPublisher eventPublisher;
    
    public void yourBusinessMethod() {
        // 비즈니스 로직...
        
        // 이벤트 발행
        eventPublisher.publishEvent(
            new LessonConfirmedEvent(this, userNo, lessonNo, "영어 회화", "2025-12-05")
        );
    }
}
```

### 실제 사용 예시

#### 1. 수업 확정
```java
@Service
@RequiredArgsConstructor
public class LessonService {
    private final LessonRepository lessonRepository;
    private final ApplicationEventPublisher eventPublisher;
    
    @Transactional
    public void confirmLesson(Long lessonNo) {
        Lesson lesson = lessonRepository.findById(lessonNo)
            .orElseThrow(() -> new IllegalArgumentException("수업을 찾을 수 없습니다"));
        
        lesson.confirm();
        lessonRepository.save(lesson);
        
        // 이벤트 발행 (비동기로 알림 전송됨)
        eventPublisher.publishEvent(new LessonConfirmedEvent(
            this,
            lesson.getStudentNo(),
            lesson.getId(),
            lesson.getTitle(),
            lesson.getStartTime().toString()
        ));
    }
}
```

#### 2. 결제 완료
```java
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final ApplicationEventPublisher eventPublisher;
    
    @Transactional
    public void completePayment(Payment payment) {
        payment.complete();
        paymentRepository.save(payment);
        
        // 결제 완료 이벤트 발행
        eventPublisher.publishEvent(new PaymentCompletedEvent(
            this,
            payment.getUserNo(),
            payment.getItemName(),
            payment.getAmount()
        ));
    }
}
```

#### 3. 계약 체결
```java
@Service
@RequiredArgsConstructor
public class ContractService {
    private final ApplicationEventPublisher eventPublisher;
    
    @Transactional
    public void signContract(Contract contract) {
        contract.sign();
        contractRepository.save(contract);
        
        // 튜터와 학생 모두에게 알림 전송
        eventPublisher.publishEvent(new ContractSignedEvent(
            this,
            contract.getTutorNo(),
            contract.getStudentNo(),
            contract.getTitle()
        ));
    }
}
```

---

## 📋 방식 3: 어노테이션 기반 상세 가이드 (가장 추천!)

### @SendNotification 속성

| 속성 | 필수 | 설명 | 예시 |
|------|------|------|------|
| `type` | ✅ | 알림 타입 | `NotificationType.LESSON_CONFIRMED` |
| `title` | ✅ | 알림 제목 | `"수업이 확정되었습니다"` |
| `message` | ✅ | 알림 메시지 | `"영어 회화 수업이 확정되었습니다"` |
| `userNoField` | ✅ | 사용자 번호 (SpEL) | `"#result.studentNo"` |
| `deepLink` | ❌ | 딥링크 | `"/lessons/#{#result.id}"` |
| `imageUrl` | ❌ | 이미지 URL | `"https://..."` |

### SpEL 표현식 사용법

#### 1. 메서드 반환값 사용 (`#result`)
```java
@SendNotification(
    type = NotificationType.LESSON_CONFIRMED,
    title = "수업이 확정되었습니다",
    message = "#{#result.title} 수업이 #{#result.startTime}에 확정되었습니다",
    userNoField = "#result.studentNo",
    deepLink = "/lessons/#{#result.id}"
)
public Lesson confirmLesson(Long lessonNo) {
    Lesson lesson = lessonRepository.findById(lessonNo);
    lesson.confirm();
    return lesson;
}
```

#### 2. 메서드 파라미터 사용
```java
@SendNotification(
    type = NotificationType.PAYMENT_COMPLETED,
    title = "결제가 완료되었습니다",
    message = "#{#itemName} 결제가 완료되었습니다. (#{#amount}원)",
    userNoField = "#userNo"
)
public Payment processPayment(Long userNo, String itemName, int amount) {
    // 결제 처리 로직
    return payment;
}
```

#### 3. 복잡한 객체 접근
```java
@SendNotification(
    type = NotificationType.CONTRACT_SIGNED,
    title = "계약이 체결되었습니다",
    message = "#{#result.title} 계약이 체결되었습니다",
    userNoField = "#result.student.userNo",
    deepLink = "/contracts/#{#result.id}"
)
public Contract signContract(Long contractNo) {
    Contract contract = contractRepository.findById(contractNo);
    contract.sign();
    return contract;
}
```

### 실제 사용 예시

#### 1. 수업 확정 (가장 간단!)
```java
@Service
public class LessonService {
    
    @SendNotification(
        type = NotificationType.LESSON_CONFIRMED,
        title = "수업이 확정되었습니다",
        message = "#{#result.title} 수업이 확정되었습니다",
        userNoField = "#result.studentNo"
    )
    @Transactional
    public Lesson confirmLesson(Long lessonNo) {
        Lesson lesson = lessonRepository.findById(lessonNo)
            .orElseThrow(() -> new IllegalArgumentException("수업을 찾을 수 없습니다"));
        
        lesson.confirm();
        return lessonRepository.save(lesson);
    }
}
```

#### 2. 수업 취소
```java
@SendNotification(
    type = NotificationType.LESSON_CANCELLED,
    title = "수업이 취소되었습니다",
    message = "#{#result.title} 수업이 취소되었습니다. 사유: #{#reason}",
    userNoField = "#result.studentNo"
)
@Transactional
public Lesson cancelLesson(Long lessonNo, String reason) {
    Lesson lesson = lessonRepository.findById(lessonNo)
        .orElseThrow(() -> new IllegalArgumentException("수업을 찾을 수 없습니다"));
    
    lesson.cancel(reason);
    return lessonRepository.save(lesson);
}
```

#### 3. 결제 완료
```java
@SendNotification(
    type = NotificationType.PAYMENT_COMPLETED,
    title = "결제가 완료되었습니다",
    message = "#{#itemName} 결제가 완료되었습니다. (금액: #{#amount}원)",
    userNoField = "#userNo",
    deepLink = "/payments"
)
@Transactional
public Payment processPayment(Long userNo, String itemName, int amount) {
    Payment payment = Payment.builder()
        .userNo(userNo)
        .itemName(itemName)
        .amount(amount)
        .build();
    
    return paymentRepository.save(payment);
}
```

#### 4. 리뷰 요청
```java
@SendNotification(
    type = NotificationType.REVIEW_REQUEST,
    title = "수업 리뷰를 남겨주세요",
    message = "#{#result.title} 수업은 어떠셨나요?",
    userNoField = "#result.studentNo",
    deepLink = "/reviews/write/#{#result.id}"
)
@Transactional
public Lesson completeLessonAndRequestReview(Long lessonNo) {
    Lesson lesson = lessonRepository.findById(lessonNo)
        .orElseThrow(() -> new IllegalArgumentException("수업을 찾을 수 없습니다"));
    
    lesson.complete();
    return lessonRepository.save(lesson);
}
```

---

## 🆚 방식 비교

| 특징 | 직접 호출 | 이벤트 | 어노테이션 |
|------|----------|--------|-----------|
| 코드 간결성 | ⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| 유지보수성 | ⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| 비동기 처리 | ❌ | ✅ | ✅ |
| 관심사 분리 | ❌ | ✅ | ✅ |
| 테스트 용이성 | ⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| 러닝커브 | 낮음 | 중간 | 낮음 |

---

## 💡 권장 사항

### 🥇 1순위: 어노테이션 방식
- 가장 간결하고 선언적
- 비즈니스 로직에 알림 코드가 전혀 없음
- SpEL로 유연한 데이터 추출

```java
@SendNotification(...)
public Lesson confirmLesson(Long lessonNo) {
    // 비즈니스 로직만 집중
}
```

### 🥈 2순위: 이벤트 방식
- 더 복잡한 로직이 필요한 경우
- 여러 리스너가 동일 이벤트를 처리해야 할 때
- 이벤트 소싱, CQRS 패턴 적용 시

```java
eventPublisher.publishEvent(new LessonConfirmedEvent(...));
```

### 🥉 3순위: 직접 호출
- 간단한 테스트나 프로토타입
- 특별한 커스터마이징이 필요한 경우

---

## 🔧 설정 필요사항

### 1. @EnableAsync 활성화 (이미 완료)
```java
@Configuration
@EnableAsync
public class AsyncConfig {
}
```

### 2. 의존성 확인 (이미 완료)
```groovy
implementation 'org.springframework.boot:spring-boot-starter-aop'
```

---

## 📝 실전 예제 모음

### 예제 1: 수업 도메인
```java
@Service
@RequiredArgsConstructor
public class LessonService {
    private final LessonRepository lessonRepository;
    
    @SendNotification(
        type = NotificationType.LESSON_CONFIRMED,
        title = "수업이 확정되었습니다",
        message = "#{#result.title} 수업이 #{#result.startTime}에 확정되었습니다",
        userNoField = "#result.studentNo"
    )
    @Transactional
    public Lesson confirmLesson(Long lessonNo) {
        Lesson lesson = lessonRepository.findById(lessonNo).orElseThrow();
        lesson.confirm();
        return lessonRepository.save(lesson);
    }
    
    @SendNotification(
        type = NotificationType.LESSON_CANCELLED,
        title = "수업이 취소되었습니다",
        message = "#{#result.title} 수업이 취소되었습니다",
        userNoField = "#result.studentNo"
    )
    @Transactional
    public Lesson cancelLesson(Long lessonNo) {
        Lesson lesson = lessonRepository.findById(lessonNo).orElseThrow();
        lesson.cancel();
        return lessonRepository.save(lesson);
    }
}
```

### 예제 2: 결제 도메인
```java
@Service
@RequiredArgsConstructor
public class PaymentService {
    
    @SendNotification(
        type = NotificationType.PAYMENT_COMPLETED,
        title = "결제가 완료되었습니다",
        message = "#{#itemName} 결제 완료 (#{#amount}원)",
        userNoField = "#userNo"
    )
    @Transactional
    public Payment processPayment(Long userNo, String itemName, int amount) {
        Payment payment = Payment.create(userNo, itemName, amount);
        return paymentRepository.save(payment);
    }
}
```

### 예제 3: 계약 도메인
```java
@Service
@RequiredArgsConstructor
public class ContractService {
    
    // 복잡한 경우: 이벤트 방식 사용
    @Transactional
    public Contract signContract(Long contractNo) {
        Contract contract = contractRepository.findById(contractNo).orElseThrow();
        contract.sign();
        contractRepository.save(contract);
        
        // 튜터와 학생 모두에게 알림 (이벤트가 더 적합)
        eventPublisher.publishEvent(new ContractSignedEvent(
            this, contract.getTutorNo(), contract.getStudentNo(), contract.getTitle()
        ));
        
        return contract;
    }
}
```

---

## ✅ 요약

1. **어노테이션 방식**을 기본으로 사용 (가장 간단!)
2. 여러 사용자에게 알림이 필요하면 **이벤트 방식** 사용
3. 비즈니스 로직에 알림 코드를 섞지 말 것
4. 모든 알림은 **비동기**로 전송됨 (메인 로직 블로킹 없음)

코드가 훨씬 깔끔해지고 유지보수가 쉬워집니다! 🚀

