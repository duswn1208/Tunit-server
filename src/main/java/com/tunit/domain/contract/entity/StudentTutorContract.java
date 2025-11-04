package com.tunit.domain.contract.entity;

import com.tunit.domain.contract.define.ContractSource;
import com.tunit.domain.contract.define.ContractStatus;
import com.tunit.domain.contract.define.ContractType;
import com.tunit.domain.contract.define.PaymentStatus;
import com.tunit.domain.contract.dto.ContractCreateRequestDto;
import com.tunit.domain.contract.exception.ContractException;
import com.tunit.domain.lesson.define.LessonSubCategory;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;

@Entity
@Table(name = "student_tutor_contract")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudentTutorContract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contractNo;

    private Long tutorProfileNo;
    private Long studentNo;
    private LocalDate startDt;
    private LocalDate endDt;

    @Enumerated(EnumType.STRING)
    private ContractStatus contractStatus;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private ContractType contractType;

    @Enumerated(EnumType.STRING)
    private LessonSubCategory lessonSubCategory;

    private Integer lessonCount;
    private Integer weekCount;
    private String lessonName;

    private String level; // 학생 레벨
    private String place; // 레슨 장소
    private String emergencyContact; // 비상 연락처

    // 정규레슨의 경우 lessonDtList에서 가장 빠른 날짜 기준으로 계산
    private Integer dayOfWeekNum; // 요일
    private LocalTime startTime; // 시작 시간
    private LocalTime endTime; // 종료 시간

    private String memo;

    // 계약 신청 경로
    @Enumerated(EnumType.STRING)
    private ContractSource source;

    // 가격 정보
    private Integer totalPrice; // 총 계약 금액

    // 결제 정보
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    private Integer paidAmount; // 실제 결제 금액
    private LocalDateTime paymentDt; // 결제 일시

    // 취소/환불 정보
    private LocalDateTime canceledAt; // 취소 일시
    private String cancelReason; // 취소 사유
    private Integer refundAmount; // 환불 금액

    // 고정레슨 연결
    private Long fixedLessonNo;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // 상태 변경 메서드들

    /**
     * 결제 상태 변경
     */
    public void updatePaymentStatus(PaymentStatus newStatus) {
        this.paymentStatus = newStatus;
    }

    /**
     * 결제 승인 (튜터가 입금 확인)
     */
    public void approvePayment(Integer paidAmount) {
        this.paymentStatus = PaymentStatus.COMPLETED;
        this.paidAmount = paidAmount;
        this.paymentDt = LocalDateTime.now();
        this.contractStatus = ContractStatus.ACTIVE; // 결제 완료 시 계약도 활성화
    }

    /**
     * 계약 취소
     */
    public void cancelContract(String reason) {
        this.contractStatus = ContractStatus.CANCELLED;
        this.canceledAt = LocalDateTime.now();
        this.cancelReason = reason;
    }

    /**
     * 계약 종료
     */
    public void endContract() {
        this.contractStatus = ContractStatus.END;
    }

    /**
     * 계약 상태 변경
     */
    public void updateContractStatus(ContractStatus newStatus) {
        this.contractStatus = newStatus;
    }

    @Builder
    public StudentTutorContract(Long tutorProfileNo, Long studentNo, LocalDate startDt, LocalDate endDt,
                                ContractStatus contractStatus, ContractType contractType,
                                LessonSubCategory lessonSubCategory, Integer lessonCount, Integer weekCount,
                                String lessonName, String level, String place, String emergencyContact,
                                Integer dayOfWeekNum, LocalTime startTime, LocalTime endTime,
                                String memo, ContractSource source, Integer totalPrice,
                                PaymentStatus paymentStatus, Integer paidAmount, LocalDateTime paymentDt,
                                LocalDateTime canceledAt, String cancelReason,
                                Integer refundAmount, Long fixedLessonNo) {
        this.tutorProfileNo = tutorProfileNo;
        this.studentNo = studentNo;
        this.startDt = startDt;
        this.endDt = endDt;
        this.contractStatus = contractStatus;
        this.contractType = contractType;
        this.lessonSubCategory = lessonSubCategory;
        this.lessonCount = lessonCount;
        this.weekCount = weekCount;
        this.lessonName = lessonName;
        this.level = level;
        this.place = place;
        this.emergencyContact = emergencyContact;
        this.dayOfWeekNum = dayOfWeekNum;
        this.startTime = startTime;
        this.endTime = endTime;
        this.memo = memo;
        this.source = source;
        this.totalPrice = totalPrice;
        this.paymentStatus = paymentStatus;
        this.paidAmount = paidAmount;
        this.paymentDt = paymentDt;
        this.canceledAt = canceledAt;
        this.cancelReason = cancelReason;
        this.refundAmount = refundAmount;
        this.fixedLessonNo = fixedLessonNo;
    }

    public static StudentTutorContract createContractOf(ContractCreateRequestDto requestDto) {
        Assert.notNull(requestDto.getTutorProfileNo(), "튜터 프로필 번호는 필수입니다.");
        Assert.notNull(requestDto.getStudentNo(), "학생 번호는 필수입니다.");
        Assert.notNull(requestDto.getContractType(), "계약 유형은 필수입니다.");
        Assert.notNull(requestDto.getLessonCategory(), "레슨 카테고리는 필수입니다.");
        Assert.notNull(requestDto.getTotalPrice(), "총 가격은 필수입니다.");

        validate(requestDto);

        //정규레슨의 경우 가장 빠른 레슨 일정 기준으로 시작일, 요일, 시간 설정
        LocalDateTime earliestLesson = requestDto.getLessonDtList().stream()
                .min(Comparator.naturalOrder())
                .orElseThrow(() -> new ContractException("레슨 일정이 비어있습니다."));

        return StudentTutorContract.builder()
                .tutorProfileNo(requestDto.getTutorProfileNo())
                .studentNo(requestDto.getStudentNo())
                .startDt(earliestLesson.toLocalDate())
                .contractStatus(ContractStatus.REQUESTED)
                .contractType(requestDto.getContractType())
                .lessonSubCategory(requestDto.getLessonCategory())
                .weekCount(requestDto.getWeekCount())
                .lessonCount(requestDto.getLessonCount())
                .lessonName(requestDto.generateLessonName())
                .level(requestDto.getLevel())
                .place(requestDto.getPlace())
                .emergencyContact(requestDto.getEmergencyContact())
                .dayOfWeekNum(earliestLesson.getDayOfWeek().getValue())
                .startTime(earliestLesson.toLocalTime())
                .endTime(earliestLesson.toLocalTime().plusHours(1)) //todo: 선생님 기본 레슨 시간으로 변경
                .memo(requestDto.getMemo())
                .source(requestDto.getSource())
                .totalPrice(requestDto.getTotalPrice())
                .paymentStatus(PaymentStatus.PENDING)
                .build();
    }

    private static void validate(ContractCreateRequestDto requestDto) {
        if (requestDto.getLessonDtList() == null || requestDto.getLessonDtList().isEmpty()) {
            throw new ContractException("레슨 매칭은 최소 하나 이상의 레슨 일���이 필요합니다.");
        }

        if (requestDto.getContractType().isRegular()) {
            if (requestDto.getLessonDtList().size() != requestDto.getWeekCount() * 4) {
                throw new ContractException("레슨 일정 수가 주당 레슨 횟수와 일치하지 않습니다.");
            }
        }

        if (!requestDto.getContractType().isRegular()) {
            if (requestDto.getLessonDtList().size() != 1) {
                throw new ContractException("선착순/단건 레슨 계약은 하나의 레슨 일정만 허용됩니다.");
            }
        }
    }
}
