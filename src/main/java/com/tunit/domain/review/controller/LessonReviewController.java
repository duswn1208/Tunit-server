package com.tunit.domain.review.controller;

import com.tunit.common.dto.PageResponse;
import com.tunit.common.session.annotation.LoginUser;
import com.tunit.common.util.PageUtil;
import com.tunit.domain.review.dto.ReviewCreateRequestDto;
import com.tunit.domain.review.dto.ReviewResponseDto;
import com.tunit.domain.review.dto.ReviewSummaryDto;
import com.tunit.domain.review.dto.ReviewUpdateRequestDto;
import com.tunit.domain.review.dto.TutorReviewsResponseDto;
import com.tunit.domain.review.service.LessonReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Void> deleteReview(@LoginUser(field = "userNo") Long userNo,
                                             @PathVariable Long reviewNo) {

        reviewService.deleteReview(reviewNo, userNo);
        return ResponseEntity.noContent().build();
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
     * 튜터에 대한 모든 리뷰 조회 (요약 정보 포함)
     * GET /api/reviews/tutor
     */
    @GetMapping("/tutor/{tutorProfileNo}")
    public ResponseEntity<TutorReviewsResponseDto> getTutorReviews(@PathVariable Long tutorProfileNo) {

        // 리뷰 목록 조회
        List<ReviewResponseDto> reviews = reviewService.getReviewsByTutorProfileNo(tutorProfileNo);

        // 통계 정보 조회
        Double averageRating = reviewService.getAverageRatingByTutorProfileNo(tutorProfileNo);
        long reviewCount = reviewService.getReviewCountByTutorProfileNo(tutorProfileNo);

        // 요약 DTO 생성
        ReviewSummaryDto summary = ReviewSummaryDto.of(reviewCount, averageRating);

        // PageResponse 생성 (페이징 없이 전체 리스트)
        PageResponse<ReviewResponseDto> pageResponse = PageUtil.createPageResponse(
                reviews,
                0,
                reviews.size(),
                reviewCount
        );

        // 통합 응답 DTO 생성
        TutorReviewsResponseDto response = TutorReviewsResponseDto.of(summary, pageResponse);

        return ResponseEntity.ok(response);
    }

    /**
     * 학생이 작성한 모든 리뷰 조회
     * GET /api/reviews/student/my
     */
    @GetMapping("/student/my")
    public ResponseEntity<PageResponse<ReviewResponseDto>> getMyReviews(
            @LoginUser(field = "userNo") Long userNo) {

        List<ReviewResponseDto> reviews = reviewService.getReviewsByStudentNo(userNo);

        PageResponse<ReviewResponseDto> response = PageUtil.createPageResponse(
                reviews,
                0,
                reviews.size(),
                reviews.size()
        );

        return ResponseEntity.ok(response);
    }
}

