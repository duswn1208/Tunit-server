package com.tunit.domain.lesson.controller;

import com.tunit.domain.lesson.define.LessonCategory;
import com.tunit.domain.lesson.define.LessonSubCategory;
import com.tunit.domain.tutor.dto.TutorLessonsResponseDto;
import com.tunit.domain.tutor.service.TutorProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/lessons")
public class LessonCategoryController {
    private final TutorProfileService tutorProfileService;

    @GetMapping("/categories")
    public ResponseEntity<List<LessonCategory>> getLessonCategories() {
        return ResponseEntity.ok(List.of(LessonCategory.values()));
    }

    @GetMapping("/tutor/categories")
    public ResponseEntity<List<TutorLessonsResponseDto>> getTutorLessonCategories(@com.tunit.common.session.annotation.LoginUser(field = "tutorProfileNo") Long tutorProfileNo) {
        return ResponseEntity.ok(tutorProfileService.findTutorLessonsByTutorProfileNo(tutorProfileNo));
    }

    @GetMapping("/categories/{code}/subcategories")
    public ResponseEntity<List<LessonSubCategory>> getLessonSubcategories(@PathVariable("code") String code) {
        return ResponseEntity.ok(LessonCategory.fromCode(code).getSubCategories());
    }
}

