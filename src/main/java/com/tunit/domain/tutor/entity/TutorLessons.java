package com.tunit.domain.tutor.entity;

import com.tunit.domain.tutor.define.LessonSubcategory;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
public class TutorLessons {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tutorLessonNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_profile_no", nullable = false)
    private TutorProfile tutorProfile;

    private LessonSubcategory lessonSubCategory;
    private Boolean isMain;
    private LocalDateTime createdAt;

    @Builder(builderMethodName = "of")
    public TutorLessons(Integer tutorLessonNo, TutorProfile tutorProfile, LessonSubcategory lessonSubCategory, Boolean isMain, LocalDateTime createdAt) {
        this.tutorLessonNo = tutorLessonNo;
        this.tutorProfile = tutorProfile;
        this.lessonSubCategory = lessonSubCategory;
        this.isMain = isMain;
        this.createdAt = createdAt;
    }

    public static TutorLessons saveFrom(TutorProfile tutorProfile, LessonSubcategory lessonSubCategory) {
        return TutorLessons.of()
                .tutorProfile(tutorProfile)
                .lessonSubCategory(lessonSubCategory)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
