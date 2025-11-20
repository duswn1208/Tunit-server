package com.tunit.domain.review.dto;

/**
 * 튜터 리뷰 요약 정보 DTO
 */
public record ReviewSummaryDto(
        long totalCount,      // 총 리뷰 개수
        Double averageRating    // 평균 평점
) {
    public static ReviewSummaryDto of(long totalReviews, Double averageRating) {
        // 평균 평점을 소수점 첫째 자리까지 반올림
        Double roundedRating = averageRating != null
                ? Math.round(averageRating * 10) / 10.0
                : 0.0;
        return new ReviewSummaryDto(totalReviews, roundedRating);
    }
}

