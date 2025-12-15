package com.tunit.domain.contract.dto;

import com.tunit.domain.contract.define.ContractStatus;
import com.tunit.domain.contract.define.ContractType;
import com.tunit.domain.contract.define.ContractSource;
import com.tunit.domain.contract.define.PaymentStatus;
import com.tunit.domain.contract.entity.StudentTutorContract;
import com.tunit.domain.lesson.define.LessonSubCategory;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
public class ContractResponseDto {
    private Long contractNo;

    private Long tutorProfileNo;
    private Long studentNo;
    @Setter
    private String studentName;

    private LocalDate startDt;
    private LocalDate endDt;

    private List<ContractScheduleDto> scheduleList;

    private ContractStatus contractStatus;
    private ContractType contractType;
    private LessonSubCategory lessonSubCategory;

    private Integer lessonCount;
    private Integer weekCount;
    private String lessonName;
    private String level;
    private String place;
    private String emergencyContact;
    private String memo;


    private ContractSource source;
    private Integer totalPrice;
    private PaymentStatus paymentStatus;
    private Integer paidAmount;
    private LocalDateTime paymentDt;

    private Integer currentLessonCount;
    private boolean isReservable;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder(builderMethodName = "of")
    public ContractResponseDto(Long contractNo, Long tutorProfileNo, Long studentNo, String studentName, LocalDate startDt, LocalDate endDt,
                               List<ContractScheduleDto> scheduleList, ContractStatus contractStatus, ContractType contractType, LessonSubCategory lessonSubCategory,
                               Integer lessonCount, Integer weekCount, String lessonName, String level, String place, String emergencyContact, String memo,
                               ContractSource source, Integer totalPrice, PaymentStatus paymentStatus, Integer paidAmount, LocalDateTime paymentDt, Integer currentLessonCount,
                               boolean isReservable, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.contractNo = contractNo;
        this.tutorProfileNo = tutorProfileNo;
        this.studentNo = studentNo;
        this.studentName = studentName;
        this.startDt = startDt;
        this.endDt = endDt;
        this.scheduleList = scheduleList;
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
        this.currentLessonCount = currentLessonCount;
        this.isReservable = isReservable;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ContractResponseDto fromEntity(StudentTutorContract contract) {
        return ContractResponseDto.of()
                .contractNo(contract.getContractNo())
                .tutorProfileNo(contract.getTutorProfileNo())
                .studentNo(contract.getStudentNo())
                .startDt(contract.getStartDt())
                .endDt(contract.getEndDt())
                .scheduleList(contract.getScheduleList().stream()
                        .map(ContractScheduleDto::fromEntity)
                        .toList())
                .contractStatus(contract.getContractStatus())
                .contractType(contract.getContractType())
                .lessonSubCategory(contract.getLessonSubCategory())
                .lessonCount(contract.getLessonCount())
                .weekCount(contract.getWeekCount())
                .lessonName(contract.getLessonName())
                .level(contract.getLevel())
                .place(contract.getPlace())
                .emergencyContact(contract.getEmergencyContact())
                .memo(contract.getMemo())
                .source(contract.getSource())
                .totalPrice(contract.getTotalPrice())
                .paymentStatus(contract.getPaymentStatus())
                .paidAmount(contract.getPaidAmount())
                .paymentDt(contract.getPaymentDt())
                .createdAt(contract.getCreatedAt())
                .updatedAt(contract.getUpdatedAt())
                .build();
    }


    @Builder(builderMethodName = "studentNameBuilder", builderClassName = "StudentNameContractResponseDtoBuilder")
    public ContractResponseDto(StudentTutorContract contract, String studentName) {
        this.contractNo = contract.getContractNo();
        this.tutorProfileNo = contract.getTutorProfileNo();
        this.studentNo = contract.getStudentNo();
        this.studentName = studentName;
        this.startDt = contract.getStartDt();
        this.endDt = contract.getEndDt();
        this.scheduleList = contract.getScheduleList().stream()
                .map(ContractScheduleDto::fromEntity)
                .toList();
        this.contractStatus = contract.getContractStatus();
        this.contractType = contract.getContractType();
        this.lessonSubCategory = contract.getLessonSubCategory();
        this.lessonCount = contract.getLessonCount();
        this.weekCount = contract.getWeekCount();
        this.lessonName = contract.getLessonName();
        this.level = contract.getLevel();
        this.place = contract.getPlace();
        this.emergencyContact = contract.getEmergencyContact();
        this.memo = contract.getMemo();
        this.source = contract.getSource();
        this.totalPrice = contract.getTotalPrice();
        this.paymentStatus = contract.getPaymentStatus();
        this.paidAmount = contract.getPaidAmount();
        this.paymentDt = contract.getPaymentDt();
        this.createdAt = contract.getCreatedAt();
        this.updatedAt = contract.getUpdatedAt();
    }

    /**
     * 현재 레슨 수 및 예약 가능 여부를 포함한 DTO 생성
     */
    public static ContractResponseDto withCurrentLessonCount(StudentTutorContract contract, Integer currentLessonCount, boolean isReservable) {
        ContractResponseDto response = ContractResponseDto.fromEntity(contract);
        response.currentLessonCount = currentLessonCount;
        response.isReservable = isReservable;
        return response;
    }
}
