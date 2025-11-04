package com.tunit.domain.contract.define;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.util.List;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ContractStatus {
    REQUESTED("REQUESTED", "신청접수"),
    APPROVED("APPROVED", "승인완료"),
    ACTIVE("ACTIVE", "진행중"),
    CANCELLED("CANCELLED", "취소완료"),
    TERMINATED("TERMINATED", "중도종료"),
    END("END", "수강종료");

    private final String code;
    private final String label;

    ContractStatus(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public static final List<ContractStatus> REQUESTED_TO_STATUTES = List.of(APPROVED, ACTIVE, CANCELLED);
    public static final List<ContractStatus> APPROVE_TO_STATUTES = List.of(ACTIVE, TERMINATED, CANCELLED, END);
    public static final List<ContractStatus> ACTIVE_TO_STATUTES = List.of(TERMINATED, END);
    public static final List<ContractStatus> TERMINATED_TO_STATUTES = List.of(ACTIVE, END);

    public boolean changeableTo(ContractStatus newStatus) {
        return switch (this) {
            case REQUESTED -> REQUESTED_TO_STATUTES.contains(newStatus);
            case APPROVED -> APPROVE_TO_STATUTES.contains(newStatus);
            case ACTIVE -> ACTIVE_TO_STATUTES.contains(newStatus);
            case TERMINATED -> TERMINATED_TO_STATUTES.contains(newStatus);
            default -> false;
        };
    }
}

