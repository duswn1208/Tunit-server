package com.tunit.domain.contract.define;

import lombok.Getter;

@Getter
public enum ContractSource {
    STUDENT_REQUEST("STUDENT_REQUEST", "학생 직접 신청"),
    TUTOR_OFFER("TUTOR_OFFER", "튜터 제안"),
    ADMIN_MATCH("ADMIN_MATCH", "관리자 매칭");

    private final String code;
    private final String label;

    ContractSource(String code, String label) {
        this.code = code;
        this.label = label;
    }
}

