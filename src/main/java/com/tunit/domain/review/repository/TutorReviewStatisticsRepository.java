package com.tunit.domain.review.repository;

import com.tunit.domain.review.entity.TutorReviewStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TutorReviewStatisticsRepository extends JpaRepository<TutorReviewStatistics, Long> {

    /**
     * 튜터 프로필 번호로 통계 조회
     */
    Optional<TutorReviewStatistics> findByTutorProfileNo(Long tutorProfileNo);
}

