package com.tunit.domain.contract.dto;

import com.tunit.domain.lesson.dto.FailedStudentDto;

import java.util.List;

public record ContractExcelUploadResultDto(
        int failCount,
        List<FailedStudentDto> failList
) {
}

