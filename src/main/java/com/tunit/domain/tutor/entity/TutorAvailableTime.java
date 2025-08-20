package com.tunit.domain.tutor.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Entity
@NoArgsConstructor
public class TutorAvailableTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tutorAvailableTimeNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_profile_no", nullable = false)
    private TutorProfile tutorProfile;

    private DayOfWeek dayOfWeek; // e.g., "MONDAY", "TUESDAY"
    private LocalTime startTime; // e.g., "09:00"
    private LocalTime endTime; // e.g., "17:00"
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder(builderMethodName = "of")
    public TutorAvailableTime(Long tutorAvailableTimeNo, TutorProfile tutorProfile, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime,
                              LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.tutorAvailableTimeNo = tutorAvailableTimeNo;
        this.tutorProfile = tutorProfile;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
