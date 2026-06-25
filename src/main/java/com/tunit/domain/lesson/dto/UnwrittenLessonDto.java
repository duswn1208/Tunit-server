package com.tunit.domain.lesson.dto;

import com.tunit.domain.lesson.entity.LessonReservation;

import java.time.LocalDate;
import java.time.LocalTime;

public record UnwrittenLessonDto(
        Long lessonReservationNo,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime
) {
    public static UnwrittenLessonDto from(LessonReservation lr) {
        return new UnwrittenLessonDto(
                lr.getLessonReservationNo(),
                lr.getDate(),
                lr.getStartTime(),
                lr.getEndTime()
        );
    }
}
