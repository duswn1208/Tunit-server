package com.tunit.domain.tutor.dto;

import com.tunit.domain.tutor.define.TutorLessonOpenType;
import com.tunit.domain.tutor.entity.TutorAvailException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record TutorAvailExceptionResponseDto(
    Long tutorHolidayNo,
    Long tutorProfileNo,
    LocalDate date,
    LocalTime startTime,
    LocalTime endTime,
    TutorLessonOpenType type,
    String reason,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

    public static TutorAvailExceptionResponseDto from(TutorAvailException entity) {
        return new TutorAvailExceptionResponseDto(
            entity.getTutorAvailExceptionNo(),
            entity.getTutorProfileNo(),
            entity.getDate(),
            entity.getStartTime(),
            entity.getEndTime(),
            entity.getType(),
            entity.getReason(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}
