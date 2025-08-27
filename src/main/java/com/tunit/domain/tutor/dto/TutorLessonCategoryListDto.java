package com.tunit.domain.tutor.dto;

import com.tunit.domain.lesson.define.LessonCategory;
import com.tunit.domain.lesson.define.LessonSubCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class TutorLessonCategoryListDto {
    private LessonCategory lessonCategory;
    private List<LessonSubCategory> lessonSubCategoryList;
}
