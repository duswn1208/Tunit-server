package com.tunit.domain.tutor.dto;

import com.tunit.domain.tutor.entity.TutorHoliday;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class TutorHolidaySaveDto {
    private LocalDateTime startHolidayDate;
    private LocalDateTime endHolidayDate;
    private String reason;

    @Builder(builderMethodName = "of")
    public TutorHolidaySaveDto(LocalDateTime startHolidayDate, LocalDateTime endHolidayDate, String reason) {
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
