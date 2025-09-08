package com.tunit.domain.lesson.dto;

import com.tunit.domain.lesson.define.LessonSubCategory;
import com.tunit.domain.lesson.define.ReservationStatus;
import com.tunit.domain.lesson.entity.LessonReservation;

import java.time.LocalDate;
import java.time.LocalTime;

public record LessonResponsDto (
    Long lessonReservationNo,
    String studentName,
    LocalTime startTime,
    LocalTime endTime,
    LocalDate date,
    ReservationStatus status,
    LessonSubCategory category
) {

    public LessonResponsDto(LessonReservation lessonReservation, String studentName) {
            this(
                    lessonReservation.getLessonReservationNo(),
                    studentName,
                    lessonReservation.getStartTime(),
                    lessonReservation.getEndTime(),
                    lessonReservation.getDate(),
                    lessonReservation.getStatus(),
                    LessonSubCategory.BASE
            );
        }
    }
