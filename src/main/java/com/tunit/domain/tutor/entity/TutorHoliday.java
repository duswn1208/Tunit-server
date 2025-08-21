package com.tunit.domain.tutor.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
public class TutorHoliday {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tutorHolidayNo;
    private Long tutorProfileNo;
    private LocalDateTime startHolidayDate;
    private LocalDateTime endHolidayDate;
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder(builderMethodName = "of")
    public TutorHoliday(Long tutorHolidayNo, Long tutorProfileNo, LocalDateTime startHolidayDate, LocalDateTime endHolidayDate, String reason, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.tutorHolidayNo = tutorHolidayNo;
        this.tutorProfileNo = tutorProfileNo;
        this.startHolidayDate = startHolidayDate;
        this.endHolidayDate = endHolidayDate;
        this.reason = reason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
