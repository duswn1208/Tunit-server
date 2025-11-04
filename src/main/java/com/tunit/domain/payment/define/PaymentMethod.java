package com.tunit.domain.payment.define;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    BANK_TRANSFER("BANK_TRANSFER", "계좌이체"),
    CARD("CARD", "카드결제"),
    CASH("CASH", "현금"),
    OTHER("OTHER", "기타");

    private final String code;
    private final String label;

    PaymentMethod(String code, String label) {
        this.code = code;
        this.label = label;
    }
}

