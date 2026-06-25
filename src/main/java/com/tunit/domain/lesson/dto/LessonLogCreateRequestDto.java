package com.tunit.domain.lesson.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LessonLogCreateRequestDto(
        @NotNull Long lessonReservationNo,
        @NotBlank String progressContent,
        String feedback
) {}
