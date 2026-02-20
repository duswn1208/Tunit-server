package com.tunit.domain.lesson.dto;

import com.tunit.domain.lesson.define.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public record GuestReservationResponseDto(
        Long reservationNo,
        String studentName,
        String tutorName,
        LocalDate lessonDate,
        LocalTime startTime,
        LocalTime endTime,
        String lessonCategory,
        ReservationStatus status,
        String memo
) {
}
