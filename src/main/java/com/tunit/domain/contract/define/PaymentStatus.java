package com.tunit.domain.contract.define;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    PENDING("PENDING", "결제 대기"),
    CONFIRMING("CONFIRMING", "입금 확인 중"),
    COMPLETED("COMPLETED", "결제 완료"),
    FAILED("FAILED", "결제 실패"),
    REFUNDED("REFUNDED", "환불 완료"),
    PARTIAL_REFUND("PARTIAL_REFUND", "부분 환불");

    private final String code;
    private final String label;

    PaymentStatus(String code, String label) {
        this.code = code;
        this.label = label;
    }
}
