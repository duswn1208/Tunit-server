package com.tunit.domain.lesson.service;

import com.tunit.domain.lesson.define.ReservationStatus;
import com.tunit.domain.lesson.entity.FixedLessonReservation;
import com.tunit.domain.lesson.entity.LessonReservation;
import com.tunit.domain.lesson.exception.LessonNotFoundException;
import com.tunit.domain.lesson.exception.LessonStatusException;
import com.tunit.domain.lesson.repository.LessonReservationRepository;
import com.tunit.domain.tutor.service.TutorProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 레슨 관리 서비스 (상태 변경, 취소, 삭제, 고정레슨)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LessonManagementService {
    private final LessonReservationRepository lessonReservationRepository;
    private final LessonQueryService lessonQueryService;

    @Transactional
    public void cancel(Long userNo, Long lessonReservationNo, ReservationStatus status) {
        LessonReservation lessonReservation = lessonQueryService.findByLessonReservationNo(lessonReservationNo);
        if (!lessonReservation.getStudentNo().equals(userNo)) {
            throw new LessonNotFoundException("Lesson reservation not found for userNo: " + userNo + " and lessonReservationNo: " + lessonReservationNo);
        }
        lessonReservation.changeStatus(lessonReservation.getStatus(), status);
    }

    public void deleteLesson(Long lessonNo) {
        if (!lessonReservationRepository.existsById(lessonNo)) {
            throw new LessonNotFoundException("Lesson not found with lessonNo: " + lessonNo);
        }
        lessonReservationRepository.deleteById(lessonNo);
    }

    @Transactional
    public void changeLessonStatus(Long lessonNo, ReservationStatus status) {
        LessonReservation lessonReservation = lessonQueryService.findByLessonReservationNo(lessonNo);
        lessonReservation.changeStatus(lessonReservation.getStatus(), status);
    }

    @Transactional
    public void changeLessonStatusByContractNo(Long contractNo, ReservationStatus status) {
        try {
            List<LessonReservation> lessonReservations = lessonQueryService.findByContractNo(contractNo);
            for (LessonReservation lessonReservation : lessonReservations) {
                lessonReservation.changeStatus(lessonReservation.getStatus(), status);
            }
        } catch (LessonStatusException e) {
            log.error("레슨 상태 변경 중 에러 발생. contractNo: {}, status: {}", contractNo, status, e);
        }
    }

}

