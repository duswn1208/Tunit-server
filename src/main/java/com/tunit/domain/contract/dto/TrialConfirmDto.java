package com.tunit.domain.contract.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.tunit.common.util.LenientLocalTimeDeserializer;
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
    @JsonDeserialize(using = LenientLocalTimeDeserializer.class)
    private LocalTime selectedStartTime;
}
