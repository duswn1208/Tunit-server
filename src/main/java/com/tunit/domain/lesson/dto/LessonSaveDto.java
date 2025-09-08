package com.tunit.domain.lesson.dto;

import com.tunit.domain.lesson.define.ReservationStatus;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public record LessonSaveDto(
        String studentName,
        String phone,
        LocalTime startTime,
        String lesson,
        LocalDate lessonDate,
        ReservationStatus reservationStatus,
        String memo // 선택
) {
}
