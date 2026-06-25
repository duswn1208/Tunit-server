package com.tunit.domain.lesson.dto;

import jakarta.validation.constraints.NotBlank;

public record LessonLogUpdateRequestDto(
        @NotBlank String progressContent,
        String feedback
) {}
