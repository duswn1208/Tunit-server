package com.tunit.domain.lesson.dto;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 미확정 체험 계약의 1순위 후보 시간을 캘린더에 "잠정(점선)"으로 표시하기 위한 DTO.
 * 실제 lesson_reservation 이 아직 없는 단계의 항목이다.
 */
public record TrialCandidateLessonDto(
        Long contractNo,
        String studentName,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        Integer priority
) {
}
