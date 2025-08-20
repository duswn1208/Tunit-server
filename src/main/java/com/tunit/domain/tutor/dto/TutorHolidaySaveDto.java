package com.tunit.domain.tutor.dto;

import com.tunit.domain.tutor.entity.TutorHoliday;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class TutorHolidaySaveDto {
    private LocalDate startHolidayDate;
    private LocalDate endHolidayDate;
    private String reason;

    @Builder(builderMethodName = "of")
    public TutorHolidaySaveDto(LocalDate startHolidayDate, LocalDate endHolidayDate, String reason) {
        this.startHolidayDate = startHolidayDate;
        this.endHolidayDate = endHolidayDate;
        this.reason = reason;
    }

    public TutorHoliday toEntity(Long tutorProfileNo) {
        return TutorHoliday.of()
                .tutorProfileNo(tutorProfileNo)
                .startHolidayDate(startHolidayDate)
                .endHolidayDate(endHolidayDate)
                .reason(reason)
                .build();
    }
}
