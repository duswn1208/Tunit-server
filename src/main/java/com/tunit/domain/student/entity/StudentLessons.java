package com.tunit.domain.student.entity;

import com.tunit.domain.lesson.define.LessonSubCategory;
import com.tunit.domain.tutor.entity.TutorProfile;
import com.tunit.domain.user.entity.UserMain;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class StudentLessons {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentLessonNo;

    private Long userNo;
    private LessonSubCategory lessonSubCategory;
    private Boolean isMain;
    private LocalDateTime createdAt;

    @Builder(builderMethodName = "of")
    public StudentLessons(Long studentLessonNo, Long userNo, LessonSubCategory lessonSubCategory, Boolean isMain, LocalDateTime createdAt) {
        this.studentLessonNo = studentLessonNo;
        this.userNo = userNo;
        this.lessonSubCategory = lessonSubCategory;
        this.isMain = isMain;
        this.createdAt = createdAt;
    }

    public static StudentLessons saveFrom(Long userNo, LessonSubCategory lessonSubCategory) {
        return StudentLessons.of()
                .userNo(userNo)
                .lessonSubCategory(lessonSubCategory)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
