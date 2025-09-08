package com.tunit.domain.lesson.dto;

import com.tunit.domain.lesson.define.ReservationStatus;

public record LessonStatusRequestDto(
        ReservationStatus status
) {
}
