package com.tunit.domain.contract.define;

public enum ContractType {
    REGULAR("REGULAR", "정규레슨"),
    FIRSTCOME("FIRSTCOME", "비정규레슨"),
    TRIAL("TRIAL", "상담/체험레슨");

    private final String code;
    private final String label;

    ContractType(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public boolean isRegular() {
        return this == REGULAR;
    }

    public boolean isFirstCome() {
        return this == FIRSTCOME;
    }

    public boolean isTrial() {
        return this == TRIAL;
    }
}

