package com.tunit.domain.lesson.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tunit.domain.lesson.define.LessonSubCategory;
import com.tunit.domain.lesson.define.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public record LessonReserveSaveDto(
        Long tutorProfileNo,

        Long studentNo,

        String studentName,  // 신규 학생 등록시 사용
        String phone,       // 신규 학생 등록시 사용

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate lessonDate,
        @JsonFormat(pattern = "HH:mm")
        LocalTime startTime,

        Integer tutorLessonNo,
        LessonSubCategory lesson,

        ReservationStatus reservationStatus,

        String memo
) {
}
