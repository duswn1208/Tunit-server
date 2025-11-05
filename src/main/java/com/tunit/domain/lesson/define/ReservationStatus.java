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
    EXPIRED("자동 만료");

    private final String label;
    private List<ReservationStatus> allowedNextStatuses;

    static {
        REQUESTED.allowedNextStatuses = List.of(ACTIVE, CANCELED, EXPIRED);
        ACTIVE.allowedNextStatuses = List.of(COMPLETED, CANCELED);
        COMPLETED.allowedNextStatuses = List.of();
        CANCELED.allowedNextStatuses = List.of();
        EXPIRED.allowedNextStatuses = List.of();
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
            COMPLETED
    );

    // 수강하지 않은 상태
    public static final List<ReservationStatus> NOT_ENROLLED_STATUSES = List.of(
        REQUESTED
    );
}
