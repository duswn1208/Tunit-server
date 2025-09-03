package com.tunit.domain.lesson.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class LessonFindRequestDto {
    private Long tutorProfileNo;
    private LocalDate startDate;
    private LocalDate endDate;
}
