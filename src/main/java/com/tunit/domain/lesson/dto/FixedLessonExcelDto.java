package com.tunit.domain.lesson.dto;

import lombok.Data;

@Data
public class FixedLessonExcelDto {
    private String name;
    private String phone;
    private String startTime;
    private String dayOfWeek;
    private String lesson;
    private String firstLessonDate;
    private String memo; // 선택
}

