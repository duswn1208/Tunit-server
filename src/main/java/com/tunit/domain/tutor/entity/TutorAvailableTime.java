package com.tunit.domain.tutor.entity;

import com.tunit.domain.tutor.exception.TutorProfileException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Entity
@NoArgsConstructor
public class TutorAvailableTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tutorAvailableTimeNo;
    private Long tutorProfileNo;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder(builderMethodName = "of")
    private TutorAvailableTime(Long tutorAvailableTimeNo, Long tutorProfileNo, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime,
                               LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.tutorAvailableTimeNo = tutorAvailableTimeNo;
        this.tutorProfileNo = tutorProfileNo;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void validateTime() {
        if (getStartTime().isAfter(getEndTime())) {
            throw new TutorProfileException("시작 시간이 끝 시간보다 늦을 수 없습니다.");
        }

        Duration duration = Duration.between(getStartTime(), getEndTime());
        if (duration.toMinutes() < 30) {
            throw new TutorProfileException("수업 가능 시간은 최소 30분 이상이어야 합니다.");
        }
    }

    public void update(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.updatedAt = LocalDateTime.now();
    }

}
