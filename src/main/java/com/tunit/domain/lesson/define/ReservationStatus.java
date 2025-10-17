package com.tunit.domain.lesson.define;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ReservationStatus {

    REQUESTED("레슨 대기"),
    ACTIVE("레슨 확정"),
    COMPLETED("완료"),
    CANCELED("취소"),
    EXPIRED("자동 만료"),
    TRIAL_REQUESTED("체험/상담 신청"),
    TRIAL_CANCELED("체험/상담 취소"),
    TRIAL_ACTIVE("체험/상담 확정"),
    TRIAL_COMPLETED("체험/상담 완료");

    private final String label;
    private List<ReservationStatus> allowedNextStatuses;

    static {
        REQUESTED.allowedNextStatuses = List.of(ACTIVE, CANCELED, EXPIRED);
        ACTIVE.allowedNextStatuses = List.of(COMPLETED, CANCELED);
        COMPLETED.allowedNextStatuses = List.of();
        CANCELED.allowedNextStatuses = List.of();
        EXPIRED.allowedNextStatuses = List.of();
        TRIAL_REQUESTED.allowedNextStatuses = List.of(TRIAL_ACTIVE, TRIAL_COMPLETED, TRIAL_CANCELED);
        TRIAL_CANCELED.allowedNextStatuses = List.of();
        TRIAL_ACTIVE.allowedNextStatuses = List.of(TRIAL_COMPLETED, TRIAL_CANCELED);
        TRIAL_COMPLETED.allowedNextStatuses = List.of();
    }

    @JsonProperty("allowedNextStatuses")
    public List<ReservationStatus> getAllowedNextStatuses() {
        return allowedNextStatuses;
    }

    @JsonProperty("name")
    public String getName() {
        return name();
    }

    //유효한 레슨상태
    public static final List<ReservationStatus> VALID_LESSON_STATUSES = List.of(
            REQUESTED,
            ACTIVE,
            COMPLETED,
            TRIAL_ACTIVE,
            TRIAL_COMPLETED,
            TRIAL_REQUESTED
    );
}
