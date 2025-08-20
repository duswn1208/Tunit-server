package com.tunit.domain.tutor.dto;

import com.tunit.domain.tutor.entity.TutorAvailableTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class TutorAvailableTimeSaveDto {
    private DayOfWeek dayOfWeek; // e.g., "MONDAY", "TUESDAY"
    private LocalTime startTime; // e.g., "09:00"
    private LocalTime endTime; // e.g., "17:00"

    @Builder(builderMethodName = "of")
    public TutorAvailableTimeSaveDto(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public TutorAvailableTime toEntity(Long tutorProfileNo) {
        return TutorAvailableTime.of()
                .tutorProfileNo(tutorProfileNo)
                .dayOfWeek(dayOfWeek)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }
}
