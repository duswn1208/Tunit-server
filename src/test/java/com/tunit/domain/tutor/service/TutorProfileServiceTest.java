package com.tunit.domain.tutor.service;

import com.tunit.domain.lesson.define.LessonCategory;
import com.tunit.domain.lesson.define.LessonSubCategory;
import com.tunit.domain.tutor.dto.TutorProfileSaveDto;
import com.tunit.domain.tutor.entity.TutorLessons;
import com.tunit.domain.tutor.entity.TutorRegion;
import com.tunit.domain.tutor.exception.TutorProfileException;
import com.tunit.domain.tutor.repository.TutorProfileRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TutorProfileServiceTest {

    @Autowired
    private TutorProfileService tutorProfileService;

    @Autowired
    private TutorProfileRepository tutorProfileRepository;

//    @Transactional
//    @Test
//    void save_success() {
//        TutorProfileSaveDto tutorProfileSaveDto = TutorProfileSaveDto.of()
//                .userNo(1L)
//                .introduce("Test Tutor")
//                .lessonCategory(LessonCategory.INSTRUMENT)
//                .lessonSubCategoryList(List.of(LessonSubCategory.BASE, LessonSubCategory.GUITAR))
//                .regionList(List.of("Seoul", "Seongnam"))
//                .careerYears(5)
//                .pricePerHour(30000)
//                .build();
//
//        Long tutorProfileNo = tutorProfileService.save(tutorProfileSaveDto);
//
//        Assertions.assertNotNull(tutorProfileNo);
//
//        var savedProfile = tutorProfileRepository.findById(tutorProfileNo);
//        assertTrue(savedProfile.isPresent());
//
//        Assertions.assertEquals(savedProfile.get().getTutorLessons().stream().map(TutorLessons::getLessonSubCategory).toList(), tutorProfileSaveDto.getLessonSubCategoryList());
//        Assertions.assertEquals(savedProfile.get().getTutorRegions().stream().map(TutorRegion::getRegion).toList(), tutorProfileSaveDto.getRegionList());
//    }
//
//    @Transactional
//    @Test
//    @DisplayName("저장 실패 - 레슨 카테고리 최소 1개 이상 선택")
//    void save_fail_because_lesson() {
//        TutorProfileSaveDto tutorProfileSaveDto = TutorProfileSaveDto.of()
//                .userNo(1L)
//                .introduce("Test Tutor")
//                .lessonCategory(LessonCategory.INSTRUMENT)
//                .lessonSubCategoryList(List.of())
//                .regionList(List.of("Seoul", "Seongnam"))
//                .careerYears(5)
//                .pricePerHour(30000)
//                .build();
//
//
//        Assertions.assertThrows(TutorProfileException.class, () -> {
//            tutorProfileService.save(tutorProfileSaveDto);
//        });
//    }
//
//    @Transactional
//    @Test
//    @DisplayName("저장 실패 - 수업 지역은 최소 1개 이상 선택")
//    void save_fail_because_lesson_count() {
//        TutorProfileSaveDto tutorProfileSaveDto = TutorProfileSaveDto.of()
//                .userNo(1L)
//                .introduce("Test Tutor")
//                .lessonCategory(LessonCategory.INSTRUMENT)
//                .lessonSubCategoryList(List.of(LessonSubCategory.BASE, LessonSubCategory.GUITAR))
//                .regionList(List.of())
//                .careerYears(5)
//                .pricePerHour(30000)
//                .build();
//
//        Assertions.assertThrows(TutorProfileException.class, () -> {
//            tutorProfileService.save(tutorProfileSaveDto);
//        });
//    }
//
//    @Transactional
//    @Test
//    @DisplayName("저장 실패 - 기본 수업단위는 30분보다 커야함")
//    void save_fail_because_duration_min() {
//        TutorProfileSaveDto tutorProfileSaveDto = TutorProfileSaveDto.of()
//                .userNo(1L)
//                .introduce("Test Tutor")
//                .lessonCategory(LessonCategory.INSTRUMENT)
//                .lessonSubCategoryList(List.of(LessonSubCategory.BASE, LessonSubCategory.GUITAR))
//                .regionList(List.of("Seoul", "Seongnam"))
//                .careerYears(5)
//                .pricePerHour(30000)
//                .durationMin(25) // 30분 단위가 아님
//                .build();
//
//        Assertions.assertThrows(TutorProfileException.class, () -> {
//            tutorProfileService.save(tutorProfileSaveDto);
//        });
//    }
//
//    @Transactional
//    @Test
//    @DisplayName("저장 실패 - 기본 수업단위는 30분 단위여야 함")
//    void save_fail_because_duration_min_not_multiple_of_30() {
//        TutorProfileSaveDto tutorProfileSaveDto = TutorProfileSaveDto.of()
//                .userNo(1L)
//                .introduce("Test Tutor")
//                .lessonCategory(LessonCategory.INSTRUMENT)
//                .lessonSubCategoryList(List.of(LessonSubCategory.BASE, LessonSubCategory.GUITAR))
//                .regionList(List.of("Seoul", "Seongnam"))
//                .careerYears(5)
//                .pricePerHour(30000)
//                .durationMin(45) // 30분 단위가 아님
//                .build();
//
//        Assertions.assertThrows(TutorProfileException.class, () -> {
//            tutorProfileService.save(tutorProfileSaveDto);
//        });
//    }
}