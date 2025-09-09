package com.tunit.domain.lesson.dto;

import com.tunit.domain.lesson.define.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public record LessonReserveSaveDto(
        String studentName,
        String phone,
        LocalTime startTime,
        String lesson,
        LocalDate lessonDate,
        ReservationStatus reservationStatus,
        String memo // 선택
) {
}
