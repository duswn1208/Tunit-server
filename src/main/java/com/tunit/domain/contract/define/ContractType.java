package com.tunit.domain.contract.define;

public enum ContractType {
    FIXED("FIXED", "고정레슨"),
    WEEKLY("WEEKLY", "주간레슨"),
    PACKAGE("PACKAGE", "패키지");

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
}

