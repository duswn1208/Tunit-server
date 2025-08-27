package com.tunit.domain.tutor.entity;

import com.tunit.domain.tutor.define.TutorLessonOpenType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@Entity
public class TutorAvailException {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tutorAvailExceptionNo;
    private Long tutorProfileNo;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private TutorLessonOpenType type;
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder(builderMethodName = "of")
    public TutorAvailException(Long tutorAvailExceptionNo, Long tutorProfileNo, LocalDate date, LocalTime startTime, LocalTime endTime, TutorLessonOpenType type, String reason, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.tutorAvailExceptionNo = tutorAvailExceptionNo;
        this.tutorProfileNo = tutorProfileNo;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
        this.reason = reason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
