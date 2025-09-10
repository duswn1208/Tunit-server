package com.tunit.domain.lesson.dto;

import com.tunit.domain.lesson.entity.FixedLessonReservation;
import com.tunit.domain.lesson.entity.LessonReservation;
import com.tunit.domain.tutor.dto.TutorAvailExceptionResponseDto;
import com.tunit.domain.tutor.dto.TutorAvailableTimeResponseDto;

import java.util.List;

public record LessonScheduleStatusDto(
        List<TutorAvailableTimeResponseDto> availableTimes,
        List<TutorAvailExceptionResponseDto> holidayDates,
        List<LessonReservation> lessonReservations,
        List<FixedLessonReservation> fixedLessonReservations
) {

}
