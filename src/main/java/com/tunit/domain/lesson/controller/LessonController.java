package com.tunit.domain.lesson.controller;

import com.tunit.common.session.annotation.LoginUser;
import com.tunit.domain.lesson.define.LessonCategory;
import com.tunit.domain.lesson.define.LessonSubCategory;
import com.tunit.domain.lesson.service.LessonReserveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonReserveService lessonReserveService;

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

    @PostMapping("/upload/excel")
    public ResponseEntity<?> uploadLessonsExcel(@LoginUser(field = "tutorProfileNo") Long tutorProfileNo,
                                                @RequestParam("file") MultipartFile file) {
        int savedCount = lessonReserveService.uploadLessonsFromExcel(tutorProfileNo, file);
        return ResponseEntity.ok("저장된 레슨 수: " + savedCount);
    }

}
