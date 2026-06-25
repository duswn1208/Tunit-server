package com.tunit.domain.lesson.dto;

import com.tunit.domain.lesson.feedback.LessonLog;

import java.time.LocalDateTime;

public record LessonLogResponseDto(
        Long logNo,
        Long lessonReservationNo,
        Long tutorProfileNo,
        Long studentNo,
        String progressContent,
        String feedback,
        String studentQuestion,
        LocalDateTime questionedAt,
        String tutorReply,
        LocalDateTime repliedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static LessonLogResponseDto from(LessonLog log) {
        return new LessonLogResponseDto(
                log.getLogNo(),
                log.getLessonReservationNo(),
                log.getTutorProfileNo(),
                log.getStudentNo(),
                log.getProgressContent(),
                log.getFeedback(),
                log.getStudentQuestion(),
                log.getQuestionedAt(),
                log.getTutorReply(),
                log.getRepliedAt(),
                log.getCreatedAt(),
                log.getUpdatedAt()
        );
    }
}
