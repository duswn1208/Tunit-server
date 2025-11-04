package com.tunit.domain.payment.define;

import lombok.Getter;

@Getter
public enum PaymentRequestStatus {
    REQUESTED("REQUESTED", "확인 요청"),
    CONFIRMED("CONFIRMED", "확인 완료"),
    REJECTED("REJECTED", "거절");

    private final String code;
    private final String label;

    PaymentRequestStatus(String code, String label) {
        this.code = code;
        this.label = label;
    }
}

