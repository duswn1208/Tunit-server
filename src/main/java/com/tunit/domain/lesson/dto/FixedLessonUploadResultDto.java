package com.tunit.domain.lesson.dto;

import java.util.List;

public record FixedLessonUploadResultDto(int failCount, List<FailedStudentDto> failList) {
}

