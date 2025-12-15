package com.tunit.domain.lesson.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tunit.domain.contract.dto.ContractExcelSaveDto;
import com.tunit.domain.lesson.define.LessonSubCategory;
import com.tunit.domain.lesson.define.ReservationStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

public record LessonReserveSaveDto(
        Long tutorProfileNo,

        Long studentNo,

        Long contractNo,

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

        @Builder(builderMethodName = "of")
        public LessonReserveSaveDto(Long tutorProfileNo, Long studentNo, Long contractNo, String studentName, String phone, LocalDate lessonDate, LocalTime startTime, Integer tutorLessonNo, LessonSubCategory lesson, ReservationStatus reservationStatus, String memo) {
                this.tutorProfileNo = tutorProfileNo;
                this.studentNo = studentNo;
                this.contractNo = contractNo;
                this.studentName = studentName;
                this.phone = phone;
                this.lessonDate = lessonDate;
                this.startTime = startTime;
                this.tutorLessonNo = tutorLessonNo;
                this.lesson = lesson;
                this.reservationStatus = reservationStatus;
                this.memo = memo;
        }

        public static LessonReserveSaveDto excelOf(Long contractNo, ContractExcelSaveDto dto) {
                return LessonReserveSaveDto.of()
                        .contractNo(contractNo)
                        .studentName(dto.studentName())
                        .phone(dto.phone())
                        .lessonDate(dto.firstLessonDate())
                        .startTime(dto.getFirstStartTime())
                        .reservationStatus(dto.reservationStatus())
                        .memo(dto.memo())
                        .build();
        }

}
