package com.tunit.domain.review.dto;

import com.tunit.domain.review.entity.LessonReview;

import java.time.LocalDateTime;

/**
 * 리뷰 응답 DTO
 */
public record ReviewResponseDto(
        Long reviewNo,
        Long lessonReservationNo,
        Long tutorProfileNo,
        Long studentNo,
        String studentName,
        Integer rating,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ReviewResponseDto from(LessonReview review, String studentName) {
        return new ReviewResponseDto(
                review.getReviewNo(),
                review.getLessonReservationNo(),
                review.getTutorProfileNo(),
                review.getStudentNo(),
                studentName,
                review.getRating(),
                review.getContent(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }

    public static ReviewResponseDto from(LessonReview review) {
        return new ReviewResponseDto(
                review.getReviewNo(),
                review.getLessonReservationNo(),
                review.getTutorProfileNo(),
                review.getStudentNo(),
                null,
                review.getRating(),
                review.getContent(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}

