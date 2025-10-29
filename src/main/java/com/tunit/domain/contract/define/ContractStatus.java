package com.tunit.domain.contract.define;

public enum ContractStatus {
    ACTIVE("ACTIVE", "진행중"),
    TERMINATED("TERMINATED", "중도종료"),
    END("END", "수강종료");

    private final String code;
    private final String label;

    ContractStatus(String code, String label) {
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

