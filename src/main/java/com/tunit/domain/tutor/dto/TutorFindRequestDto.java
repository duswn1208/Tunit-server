package com.tunit.domain.tutor.dto;

import com.tunit.domain.lesson.define.LessonSubCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class TutorFindRequestDto {
    private List<LessonSubCategory> lessonCodes;
    private List<Integer> regionCodes;

    // 후기순 등등
}
