package com.tunit.domain.tutor.review;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TutorReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewNo;

    private Long tutorProfileNo;
    private Long studentNo;
    private Long mappingNo; // optional
    private Long lessonReservationNo; // optional

    private Integer rating; // 평점(1~5)

    @Column(length = 1000)
    private String content;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

