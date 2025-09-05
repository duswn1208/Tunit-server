package com.tunit.domain.lesson.controller;

import com.tunit.common.session.annotation.LoginUser;
import com.tunit.domain.lesson.define.LessonCategory;
import com.tunit.domain.lesson.define.LessonSubCategory;
import com.tunit.domain.lesson.dto.LessonFindRequestDto;
import com.tunit.domain.lesson.dto.LessonFindResponseDto;
import com.tunit.domain.lesson.service.LessonReserveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonReserveService lessonReserveService;

    @GetMapping("")
    public ResponseEntity<?> getLessons(@LoginUser(field = "tutorProfileNo") Long tutorProfileNo,
                                        @ModelAttribute LessonFindRequestDto lessonFindRequestDto) {
        lessonFindRequestDto.setTutorProfileNo(tutorProfileNo);
        List<LessonFindResponseDto> lessons = lessonReserveService.getLessons(lessonFindRequestDto);
        log.info("tutors lessons fetched. tutorProfileNo: {}, lessons count: {}", tutorProfileNo, lessons.size());
        return ResponseEntity.ok(lessons);
    }

    //todo: cache 적용 필요
    @GetMapping("/categories")
    @ResponseBody
    public ResponseEntity<List<LessonCategory>> getLessonCategories() {
        return ResponseEntity.ok(List.of(LessonCategory.values()));
    }

    @GetMapping("/categories/{code}/subcategories")
    @ResponseBody
    public ResponseEntity<List<LessonSubCategory>> getLessonSubcategories(@PathVariable("code") String code) {
        return ResponseEntity.ok(LessonCategory.fromCode(code).getSubCategories());
    }

    @GetMapping("/exist")
    public ResponseEntity<?> existsLessons(@LoginUser(field = "tutorProfileNo") Long tutorProfileNo) {
        return ResponseEntity.ok(lessonReserveService.existsLessons(tutorProfileNo));
    }

}
