package com.tunit.domain.lesson.service;

import com.tunit.domain.lesson.dto.LessonFindRequestDto;
import com.tunit.domain.lesson.dto.LessonScheduleStatusDto;
import com.tunit.domain.lesson.entity.FixedLessonReservation;
import com.tunit.domain.lesson.entity.LessonReservation;
import com.tunit.domain.lesson.repository.FixedLessonReservationRepository;
import com.tunit.domain.lesson.repository.LessonReservationRepository;
import com.tunit.domain.tutor.dto.TutorAvailExceptionResponseDto;
import com.tunit.domain.tutor.dto.TutorAvailableTimeResponseDto;
import com.tunit.domain.tutor.repository.TutorLessonsRepository;
import com.tunit.domain.tutor.service.TutorAvailableTimeService;
import com.tunit.domain.tutor.service.TutorHolidayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class LessonService {

    private final LessonReservationRepository lessonReservationRepository;
    private final FixedLessonReservationRepository fixedLessonReservationRepository;
    private final TutorAvailableTimeService tutorAvailableTimeService;
    private final TutorHolidayService tutorHolidayService;
    private final TutorLessonsRepository tutorLessonsRepository;

    public LessonScheduleStatusDto getLessonScheduleInfo(LessonFindRequestDto lessonFindRequestDto) {

        List<TutorAvailableTimeResponseDto> availableTimes = tutorAvailableTimeService.findByTutorProfileNo(lessonFindRequestDto.getTutorProfileNo());
        List<TutorAvailExceptionResponseDto> holidayDates = tutorHolidayService.findByTutorProfileNo(lessonFindRequestDto.getTutorProfileNo());


        List<LessonReservation> lessonReservations = lessonReservationRepository.findByTutorProfileNoAndDateBetweenAndStatusIn(lessonFindRequestDto.getTutorProfileNo(), lessonFindRequestDto.getStartDate(), lessonFindRequestDto.getEndDate(), lessonFindRequestDto.getReservationStatuses());
        List<FixedLessonReservation> fixedLessonReservations = fixedLessonReservationRepository.findByTutorProfileNo(lessonFindRequestDto.getTutorProfileNo());

        LessonScheduleStatusDto lessonScheduleStatusDto = new LessonScheduleStatusDto(availableTimes, holidayDates, lessonReservations, fixedLessonReservations);
        return lessonScheduleStatusDto;
    }
}
