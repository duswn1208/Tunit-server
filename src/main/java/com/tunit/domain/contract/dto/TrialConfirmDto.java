package com.tunit.domain.contract.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrialConfirmDto {
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate selectedDate;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime selectedStartTime;
}
