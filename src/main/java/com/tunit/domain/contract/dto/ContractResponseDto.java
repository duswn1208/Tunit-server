package com.tunit.domain.contract.dto;

import com.tunit.domain.contract.define.ContractStatus;
import com.tunit.domain.contract.define.ContractType;
import com.tunit.domain.contract.define.ContractSource;
import com.tunit.domain.contract.define.PaymentStatus;
import com.tunit.domain.contract.entity.StudentTutorContract;
import com.tunit.domain.lesson.define.LessonSubCategory;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
public class ContractResponseDto {
    private Long contractNo;
    private Long tutorProfileNo;
    private Long studentNo;
    private LocalDate startDt;
    private LocalDate endDt;
    private ContractStatus contractStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ContractType contractType;
    private LessonSubCategory lessonSubCategory;
    private Integer lessonCount;
    private Integer weekCount;
    private String lessonName;
    private String level;
    private String place;
    private String emergencyContact;
    private Integer dayOfWeekNum;
    private LocalTime startTime;
    private LocalTime endTime;
    private String memo;
    private ContractSource source;
    private Integer totalPrice;
    private PaymentStatus paymentStatus;
    private Integer paidAmount;
    private LocalDateTime paymentDt;
    private LocalDateTime canceledAt;
    private String cancelReason;
    private Integer refundAmount;
    private Long fixedLessonNo;

    public ContractResponseDto(StudentTutorContract contract) {
        this.contractNo = contract.getContractNo();
        this.tutorProfileNo = contract.getTutorProfileNo();
        this.studentNo = contract.getStudentNo();
        this.startDt = contract.getStartDt();
        this.endDt = contract.getEndDt();
        this.contractStatus = contract.getContractStatus();
        this.createdAt = contract.getCreatedAt();
        this.updatedAt = contract.getUpdatedAt();
        this.contractType = contract.getContractType();
        this.lessonSubCategory = contract.getLessonSubCategory();
        this.lessonCount = contract.getLessonCount();
        this.weekCount = contract.getWeekCount();
        this.lessonName = contract.getLessonName();
        this.level = contract.getLevel();
        this.place = contract.getPlace();
        this.emergencyContact = contract.getEmergencyContact();
        this.dayOfWeekNum = contract.getDayOfWeekNum();
        this.startTime = contract.getStartTime();
        this.endTime = contract.getEndTime();
        this.memo = contract.getMemo();
        this.source = contract.getSource();
        this.totalPrice = contract.getTotalPrice();
        this.paymentStatus = contract.getPaymentStatus();
        this.paidAmount = contract.getPaidAmount();
        this.paymentDt = contract.getPaymentDt();
        this.canceledAt = contract.getCanceledAt();
        this.cancelReason = contract.getCancelReason();
        this.refundAmount = contract.getRefundAmount();
        this.fixedLessonNo = contract.getFixedLessonNo();
    }
}
