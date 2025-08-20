package com.tunit.domain.tutor.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
public class TutorHoliday {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tutorHolidayNo;
    private Long tutorProfileNo;
    private LocalDate startHolidayDate;
    private LocalDate endHolidayDate;
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder(builderMethodName = "of")
    public TutorHoliday(Long tutorHolidayNo, Long tutorProfileNo, LocalDate startHolidayDate, LocalDate endHolidayDate, String reason, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.tutorHolidayNo = tutorHolidayNo;
        this.tutorProfileNo = tutorProfileNo;
        this.startHolidayDate = startHolidayDate;
        this.endHolidayDate = endHolidayDate;
        this.reason = reason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
