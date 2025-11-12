package com.tunit.domain.tutor.entity;

import com.tunit.domain.tutor.define.TutorLessonOpenType;
import com.tunit.domain.tutor.dto.TutorAvailExceptionSaveDto;
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
    private Boolean isAllDay;
    private LocalTime startTime;
    private LocalTime endTime;
    private TutorLessonOpenType type;
    private String reason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder(builderMethodName = "of")
    public TutorAvailException(Long tutorAvailExceptionNo, Long tutorProfileNo, LocalDate date, Boolean isAllDay,
                               LocalTime startTime, LocalTime endTime, TutorLessonOpenType type, String reason, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.tutorAvailExceptionNo = tutorAvailExceptionNo;
        this.tutorProfileNo = tutorProfileNo;
        this.date = date;
        this.isAllDay = isAllDay != null ? isAllDay : (startTime == null && endTime == null);
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
        this.reason = reason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static TutorAvailException from(Long tutorProfileNo, LocalDate date, TutorAvailExceptionSaveDto saveDto) {
        return TutorAvailException.of()
                .tutorProfileNo(tutorProfileNo)
                .date(date)
                .startTime(saveDto.startTime())
                .endTime(saveDto.endTime())
                .type(TutorLessonOpenType.BLOCK)
                .reason(saveDto.reason())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
