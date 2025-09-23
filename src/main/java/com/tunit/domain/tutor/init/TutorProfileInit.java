package com.tunit.domain.tutor.init;

import com.tunit.domain.lesson.define.LessonCategory;
import com.tunit.domain.lesson.define.LessonSubCategory;
import com.tunit.domain.region.dto.RegionSaveDto;
import com.tunit.domain.tutor.entity.TutorLessons;
import com.tunit.domain.tutor.entity.TutorProfile;
import com.tunit.domain.tutor.entity.TutorRegion;
import com.tunit.domain.tutor.repository.TutorProfileRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class TutorProfileInit {
    private final TutorProfileRepository tutorProfileRepository;

    @PostConstruct
    public void init() {
        RegionSaveDto seoul = new RegionSaveDto(11, "서울특별시", "sido", 11, "서울특별시 전체");
        if (tutorProfileRepository.count() == 0) {
            TutorProfile tutor1 = TutorProfile.of()
                .userNo(1L)
                .introduce("악기 전문 튜터입니다.")
                .careerYears(5)
                .pricePerHour(30000)
                .durationMin(60)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
            tutor1.setTutorLessons(Set.of(TutorLessons.saveFrom(tutor1, LessonSubCategory.GUITAR), TutorLessons.saveFrom(tutor1, LessonSubCategory.BASE)));
            tutor1.setTutorRegions(Set.of(TutorRegion.saveFrom(tutor1, seoul), TutorRegion.saveFrom(tutor1, seoul)));

            TutorProfile tutor2 = TutorProfile.of()
                .userNo(2L)
                .introduce("운동 전문 튜터입니다.")
                .careerYears(3)
                .pricePerHour(25000)
                .durationMin(60)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
            tutor2.setTutorLessons(Set.of(TutorLessons.saveFrom(tutor2, LessonSubCategory.PT), TutorLessons.saveFrom(tutor2, LessonSubCategory.GROUP_PT)));
            tutor2.setTutorRegions(Set.of(TutorRegion.saveFrom(tutor2, seoul), TutorRegion.saveFrom(tutor2, seoul)));

            TutorProfile tutor3 = TutorProfile.of()
                .userNo(3L)
                .introduce("필라테스 전문 튜터입니다.")
                .careerYears(7)
                .pricePerHour(35000)
                .durationMin(90)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
            tutor3.setTutorLessons(Set.of(TutorLessons.saveFrom(tutor3, LessonSubCategory.PILATES), TutorLessons.saveFrom(tutor3, LessonSubCategory.YOGA)));
            tutor3.setTutorRegions(Set.of(TutorRegion.saveFrom(tutor3, seoul), TutorRegion.saveFrom(tutor3, seoul)));

            TutorProfile tutor4 = TutorProfile.of()
                .userNo(4L)
                .introduce("교사 전문 튜터입니다.")
                .careerYears(4)
                .pricePerHour(28000)
                .durationMin(60)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
            tutor4.setTutorLessons(Set.of(TutorLessons.saveFrom(tutor4, LessonSubCategory.KOREAN), TutorLessons.saveFrom(tutor4, LessonSubCategory.ENGLISH)));
            tutor4.setTutorRegions(Set.of(TutorRegion.saveFrom(tutor4, seoul), TutorRegion.saveFrom(tutor4, seoul)));

            TutorProfile tutor5 = TutorProfile.of()
                .userNo(5L)
                .introduce("요리 전문 튜터입니다.")
                .careerYears(6)
                .pricePerHour(32000)
                .durationMin(60)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
            tutor5.setTutorLessons(Set.of(TutorLessons.saveFrom(tutor5, LessonSubCategory.BAKING), TutorLessons.saveFrom(tutor5, LessonSubCategory.COOKING)));
            tutor5.setTutorRegions(Set.of(TutorRegion.saveFrom(tutor5, seoul), TutorRegion.saveFrom(tutor5, seoul)));

            tutorProfileRepository.saveAll(List.of(tutor1, tutor2, tutor3, tutor4, tutor5));
        }
    }
}
