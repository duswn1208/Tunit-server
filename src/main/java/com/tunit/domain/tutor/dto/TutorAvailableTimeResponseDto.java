package com.tunit.domain.tutor.dto;

import com.tunit.domain.tutor.entity.TutorAvailableTime;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record TutorAvailableTimeResponseDto(
        Long tutorAvailableTimeNo,
        Long tutorProfileNo,
        DayOfWeek dayOfWeek,
        Integer dayOfWeekNum,
        LocalTime startTime,
        LocalTime endTime,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static TutorAvailableTimeResponseDto from(TutorAvailableTime tutorAvailableTime) {
        return new TutorAvailableTimeResponseDto(
                tutorAvailableTime.getTutorAvailableTimeNo(),
                tutorAvailableTime.getTutorProfileNo(),
                DayOfWeek.of(tutorAvailableTime.getDayOfWeekNum()),
                tutorAvailableTime.getDayOfWeekNum(),
                tutorAvailableTime.getStartTime(),
                tutorAvailableTime.getEndTime(),
                tutorAvailableTime.getCreatedAt(),
                tutorAvailableTime.getUpdatedAt()
        );
    }

}
