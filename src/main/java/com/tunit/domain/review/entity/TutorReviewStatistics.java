package com.tunit.domain.review.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 튜터 리뷰 통계 캐싱 엔티티
 * 리뷰 생성/수정/삭제 시 자동으로 통계 업데이트
 */
@Entity
@Table(name = "tutor_review_statistics")
@Getter
@NoArgsConstructor
public class TutorReviewStatistics {

    @Id
    @Column(name = "tutor_profile_no")
    private Long tutorProfileNo;

    @Column(name = "total_reviews", nullable = false)
    private Long totalReviews = 0L;

    @Column(name = "total_rating_sum", nullable = false)
    private Long totalRatingSum = 0L;

    @Column(name = "average_rating", nullable = false)
    private Double averageRating = 0.0;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    public TutorReviewStatistics(Long tutorProfileNo, Long totalReviews,
                                 Long totalRatingSum, Double averageRating) {
        this.tutorProfileNo = tutorProfileNo;
        this.totalReviews = totalReviews;
        this.totalRatingSum = totalRatingSum;
        this.averageRating = averageRating;
    }

    /**
     * 리뷰 추가 시 통계 업데이트
     */
    public void addReview(Integer rating) {
        this.totalReviews++;
        this.totalRatingSum += rating;
        this.averageRating = Math.round((double) this.totalRatingSum / this.totalReviews * 10) / 10.0;
    }

    /**
     * 리뷰 수정 시 통계 업데이트
     */
    public void updateReview(Integer oldRating, Integer newRating) {
        this.totalRatingSum = this.totalRatingSum - oldRating + newRating;
        this.averageRating = Math.round((double) this.totalRatingSum / this.totalReviews * 10) / 10.0;
    }

    /**
     * 리뷰 삭제 시 통계 업데이트
     */
    public void removeReview(Integer rating) {
        this.totalReviews--;
        this.totalRatingSum -= rating;

        if (this.totalReviews == 0) {
            this.averageRating = 0.0;
        } else {
            this.averageRating = Math.round((double) this.totalRatingSum / this.totalReviews * 10) / 10.0;
        }
    }

    /**
     * 초기 통계 생성 (최초 리뷰 작성 시)
     */
    public static TutorReviewStatistics createInitial(Long tutorProfileNo) {
        return TutorReviewStatistics.builder()
                .tutorProfileNo(tutorProfileNo)
                .totalReviews(0L)
                .build();
    }
}

