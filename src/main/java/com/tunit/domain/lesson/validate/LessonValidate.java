package com.tunit.domain.lesson.validate;

import com.tunit.domain.lesson.define.ReservationStatus;
import com.tunit.domain.lesson.repository.LessonReservationRepository;
import com.tunit.domain.tutor.define.TutorLessonOpenType;
import com.tunit.domain.tutor.repository.TutorAvailExceptionRepository;
import com.tunit.domain.tutor.repository.TutorAvailableTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 레슨 검증 유틸 서비스
 */
@Service
@RequiredArgsConstructor
public class LessonValidate {
    private final TutorAvailableTimeRepository tutorAvailableTimeRepository;
    private final TutorAvailExceptionRepository tutorAvailExceptionRepository;
    private final LessonReservationRepository lessonReservationRepository;

    public void validateTutorAvailability(Long tutorProfileNo, LocalDate date, LocalTime startTime, LocalTime endTime) {
        // 1. 해당 요일이 튜터의 영업일이고 해당 시간이 영업시간인지 확인
        int dayOfWeek = date.getDayOfWeek().getValue();
        boolean isAvailableTime = tutorAvailableTimeRepository.existsByTutorProfileNoAndDayOfWeekNumAndRequestTimeBetweenStartTimeAndEndTime(
                tutorProfileNo, dayOfWeek, startTime);
        if (!isAvailableTime) {
            throw new IllegalArgumentException("튜터의 영업시간이 아닙니다.");
        }

        // 2. 예외일정이 있는지 확인
        boolean hasException = tutorAvailExceptionRepository.existsByTutorProfileNoAndDateAndTypeAndRequestTimeBetweenStartTimeAndEndTime(
                tutorProfileNo, date, TutorLessonOpenType.BLOCK, startTime);
        if (hasException) {
            throw new IllegalArgumentException("튜터의 예외일정이 있는 시간입니다.");
        }

        // 3. 해당 시간에 다른 레슨이 있는지 확인
        boolean hasOverlappingLesson = lessonReservationRepository.existsByTutorProfileNoAndDateAndStartTimeAndEndTimeAndStatusIn(
                tutorProfileNo,
                date,
                startTime,
                endTime,
                ReservationStatus.VALID_LESSON_STATUSES
        );
        if (hasOverlappingLesson) {
            throw new IllegalArgumentException("이미 예약된 시간입니다.");
        }
    }
}

