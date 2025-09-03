package com.tunit.domain.lesson.dto;

import com.tunit.domain.lesson.define.ReservationStatus;
import com.tunit.domain.lesson.entity.LessonReservation;

import java.time.LocalDate;
import java.time.LocalTime;

public record LessonFindResponseDto(
        Long lessonReservationNo,
        String studentName,
        LocalTime startTime,
        LocalTime endTime,
        LocalDate date,
        ReservationStatus status
) {

    public LessonFindResponseDto(LessonReservation lessonReservation, String studentName) {
        this(
                lessonReservation.getLessonReservationNo(),
                studentName,
                lessonReservation.getStartTime(),
                lessonReservation.getEndTime(),
                lessonReservation.getDate(),
                lessonReservation.getStatus()
        );
    }
}
