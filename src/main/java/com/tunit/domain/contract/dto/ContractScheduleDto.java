package com.tunit.domain.contract.dto;

import com.tunit.domain.contract.entity.ContractSchedule;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.Locale;

public record ContractScheduleDto(
        Integer dayOfWeekNum,
        String dayOfWeek,
        LocalTime startTime,
        LocalTime endTime
) {
    public static ContractScheduleDto fromEntity(ContractSchedule contractSchedule) {
        return new ContractScheduleDto(contractSchedule.getDayOfWeekNum(), DayOfWeek.of(contractSchedule.getDayOfWeekNum()).getDisplayName(TextStyle.SHORT, Locale.KOREA),
                contractSchedule.getStartTime(), contractSchedule.getEndTime());
    }
}

