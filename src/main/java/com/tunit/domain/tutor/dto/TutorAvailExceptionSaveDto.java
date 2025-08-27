package com.tunit.domain.tutor.dto;

import com.tunit.domain.tutor.define.TutorLessonOpenType;
import com.tunit.domain.tutor.entity.TutorAvailException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record TutorAvailExceptionSaveDto(
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        TutorLessonOpenType type,
        String reason) {

    public TutorAvailException toEntity(Long tutorProfileNo) {
        return TutorAvailException.of()
                .tutorProfileNo(tutorProfileNo)
                .date(this.date)
                .startTime(this.startTime)
                .endTime(this.endTime)
                .type(this.type)
                .reason(this.reason)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
