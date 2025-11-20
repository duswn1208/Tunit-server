package com.tunit.domain.review.controller;

import com.tunit.common.session.annotation.LoginUser;
import com.tunit.domain.review.dto.ReviewCreateRequestDto;
import com.tunit.domain.review.dto.ReviewResponseDto;
import com.tunit.domain.review.dto.ReviewUpdateRequestDto;
import com.tunit.domain.review.service.LessonReviewService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class LessonReviewController {

    private final LessonReviewService reviewService;

    /**
     * 리뷰 생성
     * POST /api/reviews
     */
    @PostMapping
    public ResponseEntity<ReviewResponseDto> createReview(@LoginUser(field = "userNo") Long userNo,
                                                          @Valid @RequestBody ReviewCreateRequestDto requestDto) {

        ReviewResponseDto response = reviewService.createReview(userNo, requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 리뷰 수정
     * PUT /api/reviews/{reviewNo}
     */
    @PutMapping("/{reviewNo}")
    public ResponseEntity<ReviewResponseDto> updateReview(@LoginUser(field = "userNo") Long userNo,
                                                          @PathVariable Long reviewNo,
                                                          @Valid @RequestBody ReviewUpdateRequestDto requestDto) {

        ReviewResponseDto response = reviewService.updateReview(reviewNo, userNo, requestDto);

        return ResponseEntity.ok(response);
    }

    /**
     * 리뷰 삭제
     * DELETE /api/reviews/{reviewNo}
     */
    @DeleteMapping("/{reviewNo}")
    public ResponseEntity<Map<String, String>> deleteReview(@LoginUser(field = "userNo") Long userNo,
                                                            @PathVariable Long reviewNo,
                                                            HttpSession session) {

        reviewService.deleteReview(reviewNo, userNo);

        Map<String, String> response = new HashMap<>();
        response.put("message", "리뷰가 삭제되었습니다.");

        return ResponseEntity.ok(response);
    }

    /**
     * 리뷰 단건 조회
     * GET /api/reviews/{reviewNo}
     */
    @GetMapping("/{reviewNo}")
    public ResponseEntity<ReviewResponseDto> getReview(@PathVariable Long reviewNo) {
        ReviewResponseDto response = reviewService.getReview(reviewNo);
        return ResponseEntity.ok(response);
    }

    /**
     * 레슨 예약 번호로 리뷰 조회
     * GET /api/reviews/lesson/{lessonReservationNo}
     */
    @GetMapping("/lesson/{lessonReservationNo}")
    public ResponseEntity<ReviewResponseDto> getReviewByLessonReservationNo(
            @PathVariable Long lessonReservationNo) {

        ReviewResponseDto response = reviewService.getReviewByLessonReservationNo(lessonReservationNo);
        return ResponseEntity.ok(response);
    }

    /**
     * 튜터에 대한 모든 리뷰 조회
     * GET /api/reviews/tutor/{tutorProfileNo}
     */
    @GetMapping("/tutor/{tutorProfileNo}")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByTutorProfileNo(
            @PathVariable Long tutorProfileNo) {

        List<ReviewResponseDto> reviews = reviewService.getReviewsByTutorProfileNo(tutorProfileNo);
        return ResponseEntity.ok(reviews);
    }

    /**
     * 학생이 작성한 모든 리뷰 조회
     * GET /api/reviews/student/my
     */
    @GetMapping("/student/my")
    public ResponseEntity<List<ReviewResponseDto>> getMyReviews(@LoginUser(field = "userNo") Long userNo) {
        List<ReviewResponseDto> reviews = reviewService.getReviewsByStudentNo(userNo);
        return ResponseEntity.ok(reviews);
    }

    /**
     * 튜터 평균 평점 및 리뷰 개수 조회
     * GET /api/reviews/tutor/{tutorProfileNo}/stats
     */
    @GetMapping("/tutor/{tutorProfileNo}/stats")
    public ResponseEntity<Map<String, Object>> getTutorReviewStats(
            @PathVariable Long tutorProfileNo) {

        Double averageRating = reviewService.getAverageRatingByTutorProfileNo(tutorProfileNo);
        long reviewCount = reviewService.getReviewCountByTutorProfileNo(tutorProfileNo);

        Map<String, Object> stats = new HashMap<>();
        stats.put("averageRating", averageRating != null ? Math.round(averageRating * 10) / 10.0 : 0.0);
        stats.put("reviewCount", reviewCount);

        return ResponseEntity.ok(stats);
    }
}

