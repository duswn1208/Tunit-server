package com.tunit.domain.contract.dto;

import com.tunit.domain.contract.define.ContractStatus;
import com.tunit.domain.contract.define.ContractType;
import com.tunit.domain.lesson.define.LessonSubCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ContractUpdateRequestDto {
    private LocalDate startDt;
    private LocalDate endDt;
    private ContractStatus contractStatus;
    private ContractType contractType;
    private LessonSubCategory lessonSubCategory;
    private Integer lessonCount;
    private Integer weekCount;
    private String lessonName;
    private String region;
    private String memo;
}

