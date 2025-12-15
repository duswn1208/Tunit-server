package com.tunit.domain.lesson.service;

import com.tunit.domain.lesson.define.ReservationStatus;
import com.tunit.domain.lesson.dto.LessonFindRequestDto;
import com.tunit.domain.lesson.dto.LessonFindSummaryDto;
import com.tunit.domain.lesson.dto.LessonResponseDto;
import com.tunit.domain.lesson.dto.LessonScheduleStatusDto;
import com.tunit.domain.lesson.entity.FixedLessonReservation;
import com.tunit.domain.lesson.entity.LessonReservation;
import com.tunit.domain.lesson.exception.LessonNotFoundException;
import com.tunit.domain.lesson.repository.FixedLessonReservationRepository;
import com.tunit.domain.lesson.repository.LessonReservationRepository;
import com.tunit.domain.tutor.dto.TutorAvailExceptionResponseDto;
import com.tunit.domain.tutor.dto.TutorAvailableTimeResponseDto;
import com.tunit.domain.tutor.service.TutorAvailableTimeService;
import com.tunit.domain.tutor.service.TutorHolidayService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 레슨 조회 전용 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LessonQueryService {
    private final LessonReservationRepository lessonReservationRepository;
    private final TutorAvailableTimeService tutorAvailableTimeService;
    private final TutorHolidayService tutorHolidayService;
    private final FixedLessonReservationRepository fixedLessonReservationRepository;

    public LessonFindSummaryDto getLessonSummary(LessonFindRequestDto lessonFindRequestDto) {
        List<LessonResponseDto> lessonList = lessonReservationRepository.findByTutorProfileNoAndDateBetweenWithUser(
                lessonFindRequestDto.getTutorProfileNo(),
                lessonFindRequestDto.getStartDate(),
                lessonFindRequestDto.getEndDate()
        );

        return LessonFindSummaryDto.from(lessonList);
    }

    public boolean existsLessons(@NonNull Long tutorProfileNo) {
        return lessonReservationRepository.existsByTutorProfileNo(tutorProfileNo);
    }

    public List<LessonReservation> findByContractNo(Long contractNo) {
        return lessonReservationRepository.findByContractNo(contractNo)
                .orElseThrow(() -> new LessonNotFoundException("Lesson not found with contractNo: " + contractNo));
    }

    public List<LessonReservation> findByContractNoAndStatusIn(Long contractNo, List<ReservationStatus> statuses) {
        return lessonReservationRepository.findByContractNoAndStatusIn(contractNo, statuses);
    }

    public LessonReservation findByLessonReservationNo(Long lessonReservationNo) {
        return lessonReservationRepository.findById(lessonReservationNo)
                .orElseThrow(() -> new LessonNotFoundException("Lesson not found with lessonNo: " + lessonReservationNo));
    }

    public LessonScheduleStatusDto getLessonScheduleInfo(LessonFindRequestDto lessonFindRequestDto) {

        List<TutorAvailableTimeResponseDto> availableTimes = tutorAvailableTimeService.findByTutorProfileNo(lessonFindRequestDto.getTutorProfileNo());
        List<TutorAvailExceptionResponseDto> holidayDates = tutorHolidayService.findByTutorProfileNo(lessonFindRequestDto.getTutorProfileNo());

        List<LessonReservation> lessonReservations = lessonReservationRepository.findByTutorProfileNoAndDateBetweenAndStatusIn(lessonFindRequestDto.getTutorProfileNo(), lessonFindRequestDto.getStartDate(), lessonFindRequestDto.getEndDate(), ReservationStatus.VALID_LESSON_STATUSES);
        List<FixedLessonReservation> fixedLessonReservations = fixedLessonReservationRepository.findByTutorProfileNo(lessonFindRequestDto.getTutorProfileNo());

        return new LessonScheduleStatusDto(availableTimes, holidayDates, lessonReservations, fixedLessonReservations);
    }

}

