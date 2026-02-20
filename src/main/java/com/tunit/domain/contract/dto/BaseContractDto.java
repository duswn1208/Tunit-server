package com.tunit.domain.contract.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tunit.domain.contract.define.ContractType;
import com.tunit.domain.lesson.define.LessonSubCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 계약 기본 정보 Base DTO
 * 모든 계약 생성에 공통으로 사용되는 필드
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseContractDto {
    // 필수 필드
    protected ContractType contractType;
    protected LessonSubCategory lessonCategory;
    
    // 첫 예약 정보
    @JsonFormat(pattern = "yyyy-MM-dd")
    protected LocalDate lessonDate;
    
    @JsonFormat(pattern = "HH:mm")
    protected LocalTime startTime;
    
    // 선택 필드
    protected Integer totalPrice;
    protected String place;
    protected String level;
    protected String memo;
}
