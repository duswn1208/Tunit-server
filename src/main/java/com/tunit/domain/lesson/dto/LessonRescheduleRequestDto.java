package com.tunit.domain.lesson.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record LessonRescheduleRequestDto (
    LocalDate lessonDate, LocalTime startTime, LocalTime endTime, String memo
) {}

