package com.tunit.domain.review.repository;
import com.tunit.domain.review.entity.LessonReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
public interface LessonReviewRepository extends JpaRepository<LessonReview, Long> {
    /**
     * 레슨 예약 번호로 리뷰 조회
     */
    Optional<LessonReview> findByLessonReservationNo(Long lessonReservationNo);
    /**
     * 레슨 예약 번호로 리뷰 존재 여부 확인
     */
    boolean existsByLessonReservationNo(Long lessonReservationNo);
    /**
     * 튜터에 대한 모든 리뷰 조회
     */
    List<LessonReview> findByTutorProfileNoOrderByCreatedAtDesc(Long tutorProfileNo);
    /**
     * 튜터에 대한 모든 리뷰 조회 (페이징)
     */
    Page<LessonReview> findByTutorProfileNoOrderByCreatedAtDesc(Long tutorProfileNo, Pageable pageable);
    /**
     * 학생이 작성한 모든 리뷰 조회
     */
    List<LessonReview> findByStudentNoOrderByCreatedAtDesc(Long studentNo);
    /**
     * 튜터의 평균 평점 계산
     */
    @Query("SELECT AVG(lr.rating) FROM LessonReview lr WHERE lr.tutorProfileNo = :tutorProfileNo")
    Double getAverageRatingByTutorProfileNo(@Param("tutorProfileNo") Long tutorProfileNo);
    /**
     * 튜터의 리뷰 개수
     */
    long countByTutorProfileNo(Long tutorProfileNo);
}
