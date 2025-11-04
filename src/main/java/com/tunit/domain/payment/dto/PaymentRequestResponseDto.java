package com.tunit.domain.payment.dto;

import com.tunit.domain.payment.define.PaymentMethod;
import com.tunit.domain.payment.define.PaymentRequestStatus;
import com.tunit.domain.payment.entity.PaymentRequest;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PaymentRequestResponseDto {
    private Long paymentNo;
    private Long contractNo;
    private Long studentNo;
    private Long tutorProfileNo;
    private Integer paymentAmount;
    private PaymentMethod paymentMethod;
    private String proofUrl;
    private PaymentRequestStatus status;
    private LocalDateTime requestedAt;
    private LocalDateTime confirmedAt;
    private String rejectReason;

    public PaymentRequestResponseDto(PaymentRequest paymentRequest) {
        this.paymentNo = paymentRequest.getPaymentNo();
        this.contractNo = paymentRequest.getContractNo();
        this.studentNo = paymentRequest.getStudentNo();
        this.tutorProfileNo = paymentRequest.getTutorProfileNo();
        this.paymentAmount = paymentRequest.getPaymentAmount();
        this.paymentMethod = paymentRequest.getPaymentMethod();
        this.proofUrl = paymentRequest.getProofUrl();
        this.status = paymentRequest.getStatus();
        this.requestedAt = paymentRequest.getRequestedAt();
        this.confirmedAt = paymentRequest.getConfirmedAt();
        this.rejectReason = paymentRequest.getRejectReason();
    }
}
