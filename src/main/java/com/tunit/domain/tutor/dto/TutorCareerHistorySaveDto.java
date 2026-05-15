package com.tunit.domain.tutor.dto;

import com.tunit.domain.tutor.define.TutorCareerHistoryType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class TutorCareerHistorySaveDto {

    private TutorCareerHistoryType type;
    private String title;
    private String subTitle;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private Integer displayOrder;
}
