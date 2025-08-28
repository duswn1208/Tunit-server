package com.tunit.domain.tutor.dto;

import com.tunit.domain.tutor.entity.TutorAvailableTime;
import lombok.Builder;

import java.time.LocalTime;

public record TutorAvailableTimeSaveDto(
        Integer dayOfWeekNum,
        LocalTime startTime,
        LocalTime endTime
) {
    @Builder(builderMethodName = "of")
    public TutorAvailableTimeSaveDto {}

    public TutorAvailableTime toEntity(Long tutorProfileNo) {
        return TutorAvailableTime.of()
                .tutorProfileNo(tutorProfileNo)
                .dayOfWeekNum(this.dayOfWeekNum)
                .startTime(this.startTime)
                .endTime(this.endTime)
                .build();
    }
}
