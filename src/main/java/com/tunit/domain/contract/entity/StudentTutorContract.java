package com.tunit.domain.contract.entity;

import com.tunit.domain.contract.define.ContractSource;
import com.tunit.domain.contract.define.ContractStatus;
import com.tunit.domain.contract.define.ContractType;
import com.tunit.domain.contract.define.PaymentStatus;
import com.tunit.domain.contract.dto.ContractCreateRequestDto;
import com.tunit.domain.contract.exception.ContractException;
import com.tunit.domain.lesson.define.LessonSubCategory;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.Assert;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

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
    @Setter
    private ContractStatus contractStatus;

    @Enumerated(EnumType.STRING)
    private ContractType contractType;

    @Enumerated(EnumType.STRING)
    private LessonSubCategory lessonSubCategory;

    private Integer lessonCount;
    private Integer weekCount;
    private String lessonName;
    private Integer totalPrice;

    private String level; // 학생 레벨
    private String place; // 레슨 장소
    private String emergencyContact; // 비상 연락처

    private String memo;

    // 계약 신청 경로
    @Enumerated(EnumType.STRING)
    private ContractSource source;

    // 취소/환불 정보는 별도 테이블(ContractCancellation)로 분리
    @OneToOne(mappedBy = "contract", cascade = CascadeType.ALL, orphanRemoval = true)
    private ContractCancel cancellation;

    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ContractSchedule> scheduleList = new ArrayList<>();

    // 결제 정보
    @Enumerated(EnumType.STRING)
    @Setter
    private PaymentStatus paymentStatus;
    private Integer paidAmount; // 실제 결제 금액
    private LocalDateTime paymentDt; // 결제 일시

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

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
        this.paymentStatus = PaymentStatus.PAID;
        this.paidAmount = paidAmount;
        this.paymentDt = LocalDateTime.now();
    }

    /**
     * 계약 취소
     * ContractCancellation 엔티티를 생성하여 취소 정보를 별도로 관리
     */
    public void cancelContract(String reason, Long canceledBy) {
        this.contractStatus = ContractStatus.CANCELLED;
        this.cancellation = ContractCancel.createFrom(this, reason, canceledBy);
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

    public void addSchedule(ContractSchedule schedule) {
        this.scheduleList.add(schedule);
        schedule.setContract(this);
    }

    @Builder
    public StudentTutorContract(Long tutorProfileNo, Long studentNo, LocalDate startDt, LocalDate endDt,
                                ContractStatus contractStatus, ContractType contractType,
                                LessonSubCategory lessonSubCategory, Integer lessonCount, Integer weekCount,
                                String lessonName, String level, String place, String emergencyContact,
                                String memo, ContractSource source, Integer totalPrice,
                                PaymentStatus paymentStatus, Integer paidAmount, LocalDateTime paymentDt) {
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
        this.memo = memo;
        this.source = source;
        this.totalPrice = totalPrice;
        this.paymentStatus = paymentStatus;
        this.paidAmount = paidAmount;
        this.paymentDt = paymentDt;
    }

    public static StudentTutorContract createContractOf(ContractCreateRequestDto requestDto, Integer durationMin) {
        Assert.notNull(requestDto.getTutorProfileNo(), "튜터 프로필 번호는 필수입니다.");
        Assert.notNull(requestDto.getStudentNo(), "학생 번호는 필수입니다.");
        Assert.notNull(requestDto.getContractType(), "계약 유형은 필수입니다.");
        Assert.notNull(requestDto.getLessonCategory(), "레슨 카테고리는 필수입니다.");
        Assert.notNull(requestDto.getTotalPrice(), "총 가격은 필수입니다.");

        validate(requestDto);

        // 정규레슨의 경우 가장 빠른 레슨 일정 기준으로 시작일만 설정
        LocalDateTime earliestLesson = requestDto.getLessonDtList().stream()
                .min(Comparator.naturalOrder())
                .orElseThrow(() -> new ContractException("레슨 일정이 비어있습니다."));

        StudentTutorContract contract = StudentTutorContract.builder()
                .tutorProfileNo(requestDto.getTutorProfileNo())
                .studentNo(requestDto.getStudentNo())
                .contractStatus(ContractStatus.REQUESTED)
                .contractType(requestDto.getContractType())
                .startDt(earliestLesson.toLocalDate())
                .lessonSubCategory(requestDto.getLessonCategory())
                .weekCount(requestDto.getWeekCount())
                .lessonCount(requestDto.getLessonCount())
                .lessonName(requestDto.generateLessonName())
                .level(requestDto.getLevel())
                .place(requestDto.getPlace())
                .emergencyContact(requestDto.getEmergencyContact())
                .memo(requestDto.getMemo())
                .source(requestDto.getSource())
                .totalPrice(requestDto.getTotalPrice())
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        extractLessonSchedule(requestDto.getLessonDtList(), durationMin, contract);
        return contract;
    }

    private static void extractLessonSchedule(List<LocalDateTime> lessonDtList, Integer durationMin, StudentTutorContract contract) {
        if (lessonDtList == null || lessonDtList.isEmpty()) {
            throw new ContractException("레슨 매칭은 최소 하나 이상의 레슨 일정이 필요합니다.");
        }

        // lessonDateList에서 요일+시작/종료시간 조합을 추출하여 ContractSchedule로 저장
        Set<String> uniquePattern = new HashSet<>();
        for (LocalDateTime lessonDateTime : lessonDtList) {
            int dayOfWeekNum = lessonDateTime.getDayOfWeek().getValue();
            LocalTime startTime = lessonDateTime.toLocalTime();
            LocalTime endTime = startTime.plusMinutes(durationMin); // 기본 1시간 수업
            String key = dayOfWeekNum + "-" + startTime + "-" + endTime;
            if (uniquePattern.add(key)) {
                ContractSchedule schedule = ContractSchedule.of(contract, dayOfWeekNum, startTime, endTime);
                contract.addSchedule(schedule);
            }
        }
    }

    private static void validate(ContractCreateRequestDto requestDto) {
        if (requestDto.getLessonDtList() == null || requestDto.getLessonDtList().isEmpty()) {
            throw new ContractException("레슨 매칭은 최소 하나 이상의 레슨 일정이 필요합니다.");
        }
        if (requestDto.getWeekCount() != null && requestDto.getLessonDtList().size() % requestDto.getWeekCount() != 0) {
            throw new ContractException("레슨 일정 수가 주당 레슨 횟수의 배수여야 합니다.");
        }
    }


    public void modifyContract(Long studentNo, ContractCreateRequestDto requestDto) {
        requestDto.setStudentNo(studentNo);
        validate(requestDto);

        // 정규레슨의 경우 가장 빠른 레슨 일정 기준으로 시작일만 설정
        LocalDateTime earliestLesson = requestDto.getLessonDtList().stream()
                .min(Comparator.naturalOrder())
                .orElseThrow(() -> new ContractException("레슨 일정이 비어있습니다."));

        this.startDt = earliestLesson.toLocalDate();

        this.contractType = requestDto.getContractType();
        this.contractStatus = ContractStatus.REQUESTED;

        this.lessonSubCategory = requestDto.getLessonCategory();
        this.weekCount = requestDto.getWeekCount();
        this.lessonCount = requestDto.getLessonCount();
        this.lessonName = requestDto.generateLessonName();

        this.level = requestDto.getLevel();
        this.place = requestDto.getPlace();
        this.emergencyContact = requestDto.getEmergencyContact();
        this.memo = requestDto.getMemo();

        this.source = requestDto.getSource();
        this.totalPrice = requestDto.getTotalPrice();
        this.paymentStatus = PaymentStatus.PENDING;

        ContractSchedule firstTime = this.scheduleList.get(0);
        Duration durationMin = Duration.between(firstTime.getEndTime(), firstTime.getStartTime());
        extractLessonSchedule(requestDto.getLessonDtList(), durationMin.toMinutesPart(), this);

    }

    public void updateTotalAmount(Integer newTotalAmount) {
        this.totalPrice = newTotalAmount;
        this.updatedAt = LocalDateTime.now();
    }
}
