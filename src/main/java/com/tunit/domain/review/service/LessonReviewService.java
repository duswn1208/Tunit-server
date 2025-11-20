package com.tunit.domain.review.service;

import com.tunit.domain.lesson.define.ReservationStatus;
import com.tunit.domain.lesson.entity.LessonReservation;
import com.tunit.domain.lesson.repository.LessonReservationRepository;
import com.tunit.domain.review.dto.ReviewCreateRequestDto;
import com.tunit.domain.review.dto.ReviewResponseDto;
import com.tunit.domain.review.dto.ReviewUpdateRequestDto;
import com.tunit.domain.review.entity.LessonReview;
import com.tunit.domain.review.exception.InvalidReviewException;
import com.tunit.domain.review.exception.ReviewAlreadyExistsException;
import com.tunit.domain.review.exception.ReviewNotFoundException;
import com.tunit.domain.review.repository.LessonReviewRepository;
import com.tunit.domain.user.entity.UserMain;
import com.tunit.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LessonReviewService {

    private final LessonReviewRepository reviewRepository;
    private final LessonReservationRepository lessonReservationRepository;
    private final UserRepository userRepository;

    /**
     * 리뷰 생성
     * 완료된 레슨에 대해서만 리뷰 작성 가능
     */
    @Transactional
    public ReviewResponseDto createReview(Long studentNo, ReviewCreateRequestDto requestDto) {
        // 1. 레슨 예약 정보 확인
        LessonReservation lesson = lessonReservationRepository.findById(requestDto.lessonReservationNo())
                .orElseThrow(ReviewNotFoundException::new);

        // 2. 레슨이 완료 상태인지 확인
        if (lesson.getStatus() != ReservationStatus.COMPLETED) {
            throw new InvalidReviewException("완료된 레슨에 대해서만 리뷰를 작성할 수 있습니다.");
        }

        // 3. 해당 학생의 레슨인지 확인
        if (!lesson.getStudentNo().equals(studentNo)) {
            throw new InvalidReviewException("본인의 레슨에 대해서만 리뷰를 작성할 수 있습니다.");
        }

        // 4. 이미 리뷰가 작성되었는지 확인
        if (reviewRepository.existsByLessonReservationNo(requestDto.lessonReservationNo())) {
            throw new ReviewAlreadyExistsException();
        }

        // 5. 리뷰 생성
        LessonReview review = LessonReview.builder()
                .lessonReservationNo(requestDto.lessonReservationNo())
                .tutorProfileNo(lesson.getTutorProfileNo())
                .studentNo(studentNo)
                .rating(requestDto.rating())
                .content(requestDto.content())
                .build();

        LessonReview savedReview = reviewRepository.save(review);

        log.info("리뷰 생성 완료: reviewNo={}, lessonReservationNo={}, studentNo={}, rating={}",
                savedReview.getReviewNo(), savedReview.getLessonReservationNo(), studentNo, savedReview.getRating());

        return ReviewResponseDto.from(savedReview);
    }

    /**
     * 리뷰 수정
     */
    @Transactional
    public ReviewResponseDto updateReview(Long reviewNo, Long studentNo, ReviewUpdateRequestDto requestDto) {
        LessonReview review = getLessonReview(reviewNo);

        validUserLessonReview(studentNo, review, "리뷰 수정 권한이 없습니다.");

        review.updateReview(requestDto.rating(), requestDto.content());

        log.info("리뷰 수정 완료: reviewNo={}, studentNo={}", reviewNo, studentNo);

        return ReviewResponseDto.from(review);
    }

    /**
     * 리뷰 삭제
     */
    @Transactional
    public void deleteReview(Long reviewNo, Long studentNo) {
        LessonReview review = getLessonReview(reviewNo);

        validUserLessonReview(studentNo, review, "리뷰 삭제 권한이 없습니다.");

        reviewRepository.delete(review);

        log.info("리뷰 삭제 완료: reviewNo={}, studentNo={}", reviewNo, studentNo);
    }

    private static void validUserLessonReview(Long studentNo, LessonReview review, String errorMessage) {
        // 본인의 리뷰인지 확인
        if (!review.getStudentNo().equals(studentNo)) {
            throw new InvalidReviewException(errorMessage);
        }
    }

    /**
     * 리뷰 단건 조회
     */
    public ReviewResponseDto getReview(Long reviewNo) {
        LessonReview review = getLessonReview(reviewNo);

        UserMain student = userRepository.findById(review.getStudentNo())
                .orElse(null);

        String studentName = student != null ? student.getName() : "알 수 없음";

        return ReviewResponseDto.from(review, studentName);
    }

    private LessonReview getLessonReview(Long reviewNo) {
        LessonReview review = reviewRepository.findById(reviewNo)
                .orElseThrow(() -> new ReviewNotFoundException("존재하지 않는 리뷰입니다."));
        return review;
    }

    /**
     * 레슨 예약 번호로 리뷰 조회
     */
    public ReviewResponseDto getReviewByLessonReservationNo(Long lessonReservationNo) {
        LessonReview review = reviewRepository.findByLessonReservationNo(lessonReservationNo)
                .orElseThrow(() -> new ReviewNotFoundException("해당 레슨에 대한 리뷰가 없습니다."));

        UserMain student = userRepository.findById(review.getStudentNo())
                .orElse(null);

        String studentName = student != null ? student.getName() : "알 수 없음";

        return ReviewResponseDto.from(review, studentName);
    }

    /**
     * 튜터에 대한 모든 리뷰 조회
     */
    public List<ReviewResponseDto> getReviewsByTutorProfileNo(Long tutorProfileNo) {
        List<LessonReview> reviews = reviewRepository.findByTutorProfileNoOrderByCreatedAtDesc(tutorProfileNo);

        return reviews.stream()
                .map(review -> {
                    UserMain student = userRepository.findById(review.getStudentNo())
                            .orElse(null);
                    String studentName = student != null ? student.getName() : "알 수 없음";
                    return ReviewResponseDto.from(review, studentName);
                })
                .collect(Collectors.toList());
    }

    /**
     * 학생이 작성한 모든 리뷰 조회
     */
    public List<ReviewResponseDto> getReviewsByStudentNo(Long studentNo) {
        List<LessonReview> reviews = reviewRepository.findByStudentNoOrderByCreatedAtDesc(studentNo);

        return reviews.stream()
                .map(ReviewResponseDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 튜터 평균 평점 조회
     */
    public Double getAverageRatingByTutorProfileNo(Long tutorProfileNo) {
        return reviewRepository.getAverageRatingByTutorProfileNo(tutorProfileNo);
    }

    /**
     * 튜터 리뷰 개수
     */
    public long getReviewCountByTutorProfileNo(Long tutorProfileNo) {
        return reviewRepository.countByTutorProfileNo(tutorProfileNo);
    }
}

