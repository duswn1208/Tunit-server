package com.tunit.domain.tutor.dto;

import com.tunit.domain.lesson.define.LessonSubCategory;
import com.tunit.domain.tutor.entity.TutorLessons;

public record TutorLessonsResponseDto(

        Integer tutorLessonNo,
        LessonSubCategory lessonCategory,
        Boolean isMain
) {

    public static TutorLessonsResponseDto from(TutorLessons tutorLessons) {
        return new TutorLessonsResponseDto(
                tutorLessons.getTutorLessonNo(),
                tutorLessons.getLessonSubCategory(),
                tutorLessons.getIsMain()
        );
    }
}
