package com.tunit.domain.tutor.dto;

import com.tunit.domain.tutor.entity.TutorHoliday;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public record TutorHolidayResponseDto (
    Long tutorHolidayNo,
    Long tutorProfileNo,
    LocalDateTime startHolidayDate,
    LocalDateTime endHolidayDate,
    String reason,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

    public static TutorHolidayResponseDto from(TutorHoliday entity) {
        return new TutorHolidayResponseDto(
                entity.getTutorHolidayNo(),
                entity.getTutorProfileNo(),
                entity.getStartHolidayDate(),
                entity.getEndHolidayDate(),
                entity.getReason(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
