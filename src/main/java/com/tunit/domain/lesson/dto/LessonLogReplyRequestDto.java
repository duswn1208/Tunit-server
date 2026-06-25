package com.tunit.domain.lesson.dto;

import jakarta.validation.constraints.NotBlank;

public record LessonLogReplyRequestDto(
        @NotBlank String reply
) {}
