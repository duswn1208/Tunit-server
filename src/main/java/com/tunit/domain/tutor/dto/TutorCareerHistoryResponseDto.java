package com.tunit.domain.tutor.dto;

import com.tunit.domain.tutor.define.TutorCareerHistoryType;
import com.tunit.domain.tutor.entity.TutorCareerHistory;

import java.time.LocalDate;

public record TutorCareerHistoryResponseDto(
        Long tutorCareerHistoryNo,
        TutorCareerHistoryType type,
        String title,
        String subTitle,
        LocalDate startDate,
        LocalDate endDate,
        String description,
        Integer displayOrder
) {
    public static TutorCareerHistoryResponseDto from(TutorCareerHistory entity) {
        return new TutorCareerHistoryResponseDto(
                entity.getTutorCareerHistoryNo(),
                entity.getType(),
                entity.getTitle(),
                entity.getSubTitle(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getDescription(),
                entity.getDisplayOrder()
        );
    }
}
