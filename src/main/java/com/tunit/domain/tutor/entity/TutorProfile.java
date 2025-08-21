package com.tunit.domain.tutor.entity;

import com.tunit.domain.lesson.define.LessonCategory;
import com.tunit.domain.tutor.dto.TutorProfileSaveDto;
import com.tunit.domain.tutor.exception.TutorProfileException;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
public class TutorProfile {
    private static final Integer MIN_DURATION_UNIT = 30;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tutorProfileNo;
    private Long userNo;
    private String introduce;
    private LessonCategory lessonCategory;

    @Setter
    @OneToMany(mappedBy = "tutorProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TutorLessons> tutorLessons;

    @Setter
    @OneToMany(mappedBy = "tutorProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TutorRegion> tutorRegions;

    private Integer careerYears;
    private Integer pricePerHour;
    private Integer durationMin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder(builderMethodName = "of")
    public TutorProfile(Long tutorProfileNo, Long userNo, String introduce, LessonCategory lessonCategory, List<TutorLessons> tutorLessons, List<TutorRegion> tutorRegions,
                        Integer careerYears, Integer pricePerHour, Integer durationMin, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.tutorProfileNo = tutorProfileNo;
        this.userNo = userNo;
        this.introduce = introduce;
        this.lessonCategory = lessonCategory;
        this.tutorLessons = tutorLessons;
        this.tutorRegions = tutorRegions;
        this.careerYears = careerYears;
        this.pricePerHour = pricePerHour;
        this.durationMin = durationMin;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static TutorProfile saveFrom(TutorProfileSaveDto tutorProfileSaveDto) {
        TutorProfile tutorProfile = TutorProfile.of()
                .userNo(tutorProfileSaveDto.getUserNo())
                .introduce(tutorProfileSaveDto.getIntroduce())
                .lessonCategory(tutorProfileSaveDto.getLessonCategory())
                .careerYears(tutorProfileSaveDto.getCareerYears())
                .pricePerHour(tutorProfileSaveDto.getPricePerHour())
                .durationMin(tutorProfileSaveDto.getDurationMin())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<TutorLessons> tutorLessons = tutorProfileSaveDto.getLessonSubCategoryList().stream()
                .map(sub -> TutorLessons.saveFrom(tutorProfile, sub))
                .toList();
        tutorProfile.setTutorLessons(tutorLessons);
        List<TutorRegion> tutorRegions = tutorProfileSaveDto.getRegionList().stream()
                .map(region -> TutorRegion.saveFrom(tutorProfile, region))
                .toList();
        tutorProfile.setTutorRegions(tutorRegions);

        tutorProfile.validate();

        return tutorProfile;
    }

    private void validate() {
        Integer durationMin = getDurationMin();
        if (durationMin < MIN_DURATION_UNIT || durationMin % MIN_DURATION_UNIT != 0) {
            throw new TutorProfileException("Duration must be a multiple of 30 minutes and at least 30 minutes");
        }

        if (tutorLessons.isEmpty()) {
            throw new TutorProfileException("Lesson subcategories cannot be null");
        }

        if (tutorRegions.isEmpty()) {
            throw new TutorProfileException("Regions cannot be null");
        }
    }
}
