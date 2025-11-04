package com.tunit.domain.payment.entity;

import com.tunit.domain.payment.define.PaymentMethod;
import com.tunit.domain.payment.define.PaymentRequestStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_request")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentNo;

    @Column(name = "contract_no", nullable = false)
    private Long contractNo;

    @Column(name = "student_no", nullable = false)
    private Long studentNo;

    @Column(name = "tutor_profile_no", nullable = false)
    private Long tutorProfileNo;

    @Column(name = "payment_amount", nullable = false)
    private Integer paymentAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "proof_url", length = 500)
    private String proofUrl; // 입금증 등 첨부 파일 URL

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentRequestStatus status;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "reject_reason", length = 500)
    private String rejectReason; // 거절 사유

    @PrePersist
    protected void onCreate() {
        if (this.requestedAt == null) {
            this.requestedAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = PaymentRequestStatus.REQUESTED;
        }
    }

    @Builder
    public PaymentRequest(Long contractNo, Long studentNo, Long tutorProfileNo,
                         Integer paymentAmount, PaymentMethod paymentMethod,
                         String proofUrl, PaymentRequestStatus status,
                         LocalDateTime requestedAt, LocalDateTime confirmedAt,
                         String rejectReason) {
        this.contractNo = contractNo;
        this.studentNo = studentNo;
        this.tutorProfileNo = tutorProfileNo;
        this.paymentAmount = paymentAmount;
        this.paymentMethod = paymentMethod;
        this.proofUrl = proofUrl;
        this.status = status;
        this.requestedAt = requestedAt;
        this.confirmedAt = confirmedAt;
        this.rejectReason = rejectReason;
    }

    /**
     * 결제 확인 (튜터가 확인)
     */
    public void confirm() {
        if (this.status != PaymentRequestStatus.REQUESTED) {
            throw new IllegalStateException("요청 상태에서만 확인이 가능합니다.");
        }
        this.status = PaymentRequestStatus.CONFIRMED;
        this.confirmedAt = LocalDateTime.now();
    }

    /**
     * 결제 거절 (튜터가 거절)
     */
    public void reject(String reason) {
        if (this.status != PaymentRequestStatus.REQUESTED) {
            throw new IllegalStateException("요청 상태에서만 거절이 가능합니다.");
        }
        this.status = PaymentRequestStatus.REJECTED;
        this.rejectReason = reason;
        this.confirmedAt = LocalDateTime.now();
    }
}
