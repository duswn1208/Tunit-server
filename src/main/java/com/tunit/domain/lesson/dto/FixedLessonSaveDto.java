package com.tunit.domain.lesson.dto;

import com.tunit.domain.lesson.define.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public record FixedLessonSaveDto(
        String studentName,
        String phone,
        LocalTime startTime,
        Set<Integer> dayOfWeekSet,
        String lesson,
        LocalDate firstLessonDate,
        ReservationStatus reservationStatus,
        String memo // 선택
) {
}
