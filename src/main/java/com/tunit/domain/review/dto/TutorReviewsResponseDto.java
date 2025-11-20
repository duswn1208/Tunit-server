package com.tunit.domain.review.dto;

import com.tunit.common.dto.PageResponse;

/**
 * 튜터 리뷰 목록 응답 DTO (요약 정보 + 페이징된 리뷰 리스트)
 */
public record TutorReviewsResponseDto(
        ReviewSummaryDto summary,                   // 리뷰 요약 정보
        PageResponse<ReviewResponseDto> reviews     // 페이징된 리뷰 리스트
) {
    public static TutorReviewsResponseDto of(
            ReviewSummaryDto summary,
            PageResponse<ReviewResponseDto> reviews
    ) {
        return new TutorReviewsResponseDto(summary, reviews);
    }
}

