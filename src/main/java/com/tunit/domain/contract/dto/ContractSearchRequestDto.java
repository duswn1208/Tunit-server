package com.tunit.domain.contract.dto;

import com.tunit.domain.contract.define.ContractStatus;
import com.tunit.domain.contract.define.ContractType;
import com.tunit.domain.lesson.define.LessonSubCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class ContractSearchRequestDto {
    private Long tutorProfileNo;
    private Long studentNo;
    private LocalDate startDate;
    private LocalDate endDate;
    private ContractStatus contractStatus;
    private ContractType contractType;
    private LessonSubCategory lessonSubCategory;
}

