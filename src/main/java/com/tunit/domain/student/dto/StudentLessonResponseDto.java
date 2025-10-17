package com.tunit.domain.student.dto;

import com.tunit.domain.lesson.define.LessonSubCategory;
import com.tunit.domain.lesson.define.ReservationStatus;
import com.tunit.domain.lesson.entity.LessonReservation;
import com.tunit.domain.user.entity.UserMain;

import java.time.LocalDate;
import java.time.LocalTime;

public record StudentLessonResponseDto(
        Long lessonReservationNo,
        Long tutorProfileNo,
        String tutorName,
        LessonSubCategory lessonCategory,
        LocalDate lessonDate,
        LocalTime startTime,
        LocalTime endTime,
        ReservationStatus status,
        String memo
) {
    public static StudentLessonResponseDto of(LessonReservation lesson, String tutorName) {
        return new StudentLessonResponseDto(
                lesson.getLessonReservationNo(),
                lesson.getTutorProfileNo(),
                tutorName,
                lesson.getLessonCategory(),
                lesson.getDate(),
                lesson.getStartTime(),
                lesson.getEndTime(),
                lesson.getStatus(),
                lesson.getMemo()
        );
    }

    public static StudentLessonResponseDto from(LessonReservation lesson, UserMain tutor) {
        return new StudentLessonResponseDto(
                lesson.getLessonReservationNo(),
                lesson.getTutorProfileNo(),
                tutor.getName(),
                lesson.getLessonCategory(),
                lesson.getDate(),
                lesson.getStartTime(),
                lesson.getEndTime(),
                lesson.getStatus(),
                lesson.getMemo()
        );
    }
}
