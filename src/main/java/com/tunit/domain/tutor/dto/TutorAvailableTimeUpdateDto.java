package com.tunit.domain.tutor.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class TutorAvailableTimeUpdateDto {
    private List<TutorAvailableTimeSaveDto> tutorAvailableTimeSaveDtoList;
}
