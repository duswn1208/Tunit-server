package com.tunit.domain.lesson.dto;

import jakarta.validation.constraints.NotBlank;

public record LessonLogQuestionRequestDto(
        @NotBlank String question
) {}
