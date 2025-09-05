package com.tunit.domain.lesson.dto;

import com.tunit.domain.lesson.define.LessonSubCategory;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public record FixedLessonSaveDto(
        String studentName,
        String phone,
        LocalTime startTime,
        Set<DayOfWeek> dayOfWeekSet,
        String lesson,
        LocalDate firstLessonDate,
        String memo // 선택
) {
}
