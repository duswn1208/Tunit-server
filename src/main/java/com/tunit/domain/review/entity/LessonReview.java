package com.tunit.domain.review.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "lesson_review")
@Getter
@NoArgsConstructor
public class LessonReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_no")
    private Long reviewNo;

    @Column(name = "lesson_reservation_no", nullable = false, unique = true)
    private Long lessonReservationNo;  // 완료된 레슨

    @Column(name = "tutor_profile_no", nullable = false)
    private Long tutorProfileNo;

    @Column(name = "student_no", nullable = false)
    private Long studentNo;

    @Column(name = "rating", nullable = false)
    private Integer rating;  // 1~5 점

    @Column(name = "content", length = 1000)
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    public LessonReview(Long reviewNo, Long lessonReservationNo, Long tutorProfileNo,
                       Long studentNo, Integer rating, String content,
                       LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.reviewNo = reviewNo;
        this.lessonReservationNo = lessonReservationNo;
        this.tutorProfileNo = tutorProfileNo;
        this.studentNo = studentNo;
        this.rating = rating;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 리뷰 수정
     */
    public void updateReview(Integer rating, String content) {
        if (rating != null) {
            this.rating = rating;
        }
        if (content != null) {
            this.content = content;
        }
    }
}

