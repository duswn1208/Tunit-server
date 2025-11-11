package com.tunit.domain.contract.define;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tunit.domain.contract.exception.ContractPayException;
import lombok.Getter;

import java.util.List;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PaymentStatus {
    PENDING("PENDING", "결제 대기"),
    CONFIRMING("CONFIRMING", "입금 확인 중"),
    PAID("PAID", "결제 완료"),
    FAILED("FAILED", "결제 실패"),
    REFUNDED("REFUNDED", "환불 완료"),
    PARTIAL_REFUNDED("PARTIAL_REFUNDED", "부분 환불");

    private final String code;
    private final String label;

    PaymentStatus(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public static final List<PaymentStatus> PENDING_TO_STATUES = List.of(CONFIRMING, FAILED);
    public static final List<PaymentStatus> CONFIRMING_TO_STATUTES = List.of(PAID, FAILED, REFUNDED, PARTIAL_REFUNDED);
    public static final List<PaymentStatus> PAID_TO_STATUES = List.of(PENDING, REFUNDED, PARTIAL_REFUNDED);
    public static final List<PaymentStatus> FAILED_TO_STATUES = List.of(REFUNDED, PARTIAL_REFUNDED);
    public static final List<PaymentStatus> REFUNDED_TO_STATUES = List.of(FAILED);

    public static final List<ContractStatus> CONFIRMING_ALLOWED_CONTRACT_STATUSES = List.of(ContractStatus.REQUESTED, ContractStatus.APPROVED);
    public static final List<ContractStatus> PAID_ALLOWED_CONTRACT_STATUSES = List.of(ContractStatus.APPROVED, ContractStatus.ACTIVE);
    public static final List<ContractStatus> REFUNDED_ALLOWED_CONTRACT_STATUSES = List.of(ContractStatus.TERMINATED, ContractStatus.END, ContractStatus.CANCELLED);
    public static final List<ContractStatus> PARTIAL_REFUND_ALLOWED_CONTRACT_STATUSES = List.of(ContractStatus.TERMINATED, ContractStatus.END, ContractStatus.CANCELLED);

    public void changeableByContractStatus(ContractStatus currentStatus) {
        switch (this) {
            case CONFIRMING -> {
                if (!CONFIRMING_ALLOWED_CONTRACT_STATUSES.contains(currentStatus)) {
                    throw new ContractPayException("PaymentStatus cannot be changed to " + this + " when ContractStatus is " + currentStatus);
                }
            }
            case PAID -> {
                if (!PAID_ALLOWED_CONTRACT_STATUSES.contains(currentStatus)) {
                    throw new ContractPayException("PaymentStatus cannot be changed to " + this + " when ContractStatus is " + currentStatus);
                }
            }
            case REFUNDED -> {
                if (!REFUNDED_ALLOWED_CONTRACT_STATUSES.contains(currentStatus)) {
                    throw new ContractPayException("PaymentStatus cannot be changed to " + this + " when ContractStatus is " + currentStatus);
                }
            }
            case PARTIAL_REFUNDED -> {
                if (!PARTIAL_REFUND_ALLOWED_CONTRACT_STATUSES.contains(currentStatus)) {
                    throw new ContractPayException("PaymentStatus cannot be changed to " + this + " when ContractStatus is " + currentStatus);
                }
            }
            default -> {
                // PENDING, FAILED 등은 제약 없음
            }
        }
    }

    public void changeTo(PaymentStatus newStatus) {
        if (!changeableTo(newStatus)) {
            throw new ContractPayException("PaymentStatus cannot be changed from " + this + " to " + newStatus);
        }
    }

    public boolean changeableTo(PaymentStatus newStatus) {
        return switch (this) {
            case PENDING -> PENDING_TO_STATUES.contains(newStatus);
            case CONFIRMING -> CONFIRMING_TO_STATUTES.contains(newStatus);
            case PAID -> PAID_TO_STATUES.contains(newStatus);
            case FAILED -> FAILED_TO_STATUES.contains(newStatus);
            case REFUNDED -> REFUNDED_TO_STATUES.contains(newStatus);
            default -> false;
        };
    }
}
