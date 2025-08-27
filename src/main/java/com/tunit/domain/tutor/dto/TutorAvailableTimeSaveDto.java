package com.tunit.domain.tutor.dto;

import com.tunit.domain.tutor.entity.TutorAvailableTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class TutorAvailableTimeSaveDto {
    private Integer dayOfWeekNum;
    private LocalTime startTime;
    private LocalTime endTime;

    @Builder(builderMethodName = "of")
    public TutorAvailableTimeSaveDto(Integer dayOfWeekNum, LocalTime startTime, LocalTime endTime) {
        this.dayOfWeekNum = dayOfWeekNum;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public TutorAvailableTime toEntity(Long tutorProfileNo) {
        return TutorAvailableTime.of()
                .tutorProfileNo(tutorProfileNo)
                .dayOfWeekNum(dayOfWeekNum)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }
}
