package com.tunit.domain.tutor.dto;

import com.tunit.domain.tutor.entity.TutorAvailableTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class TutorAvailableTimeUpdateDto {
    private Long tutorAvailableTimeNo;
    private Integer dayOfWeekNum;
    private LocalTime startTime;
    private LocalTime endTime;

    @Builder(builderMethodName = "of")
    public TutorAvailableTimeUpdateDto(Long tutorAvailableTimeNo, Integer dayOfWeekNum, LocalTime startTime, LocalTime endTime) {
        this.tutorAvailableTimeNo = tutorAvailableTimeNo;
        this.dayOfWeekNum = dayOfWeekNum;
        this.startTime = startTime;
        this.endTime = endTime;
    }

}
