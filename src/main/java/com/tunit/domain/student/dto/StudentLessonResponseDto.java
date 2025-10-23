package com.tunit.domain.student.dto;

import com.tunit.domain.lesson.define.LessonSubCategory;
import com.tunit.domain.lesson.define.ReservationStatus;
import com.tunit.domain.lesson.entity.LessonReservation;
import com.tunit.domain.tutor.dto.TutorProfileResponseDto;

import java.time.LocalDate;
import java.time.LocalTime;

public record StudentLessonResponseDto(
        Long lessonReservationNo,
        Long tutorProfileNo,
        LessonSubCategory lessonCategory,
        LocalDate lessonDate,
        LocalTime startTime,
        LocalTime endTime,
        ReservationStatus status,
        String memo,
        TutorProfileResponseDto tutorProfile
) {
    public static StudentLessonResponseDto of(LessonReservation lesson, TutorProfileResponseDto tutorProfile) {
        return new StudentLessonResponseDto(
                lesson.getLessonReservationNo(),
                lesson.getTutorProfileNo(),
                lesson.getLessonCategory(),
                lesson.getDate(),
                lesson.getStartTime(),
                lesson.getEndTime(),
                lesson.getStatus(),
                lesson.getMemo(),
                tutorProfile
        );
    }

    public static StudentLessonResponseDto from(LessonReservation lesson) {
        return new StudentLessonResponseDto(
                lesson.getLessonReservationNo(),
                lesson.getTutorProfileNo(),
                lesson.getLessonCategory(),
                lesson.getDate(),
                lesson.getStartTime(),
                lesson.getEndTime(),
                lesson.getStatus(),
                lesson.getMemo(),
                null
        );
    }
}
