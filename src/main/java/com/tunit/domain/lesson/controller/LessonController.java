package com.tunit.domain.lesson.controller;

import com.tunit.common.session.annotation.LoginUser;
import com.tunit.domain.lesson.define.LessonCategory;
import com.tunit.domain.lesson.define.LessonSubCategory;
import com.tunit.domain.lesson.define.ReservationStatus;
import com.tunit.domain.lesson.dto.LessonFindRequestDto;
import com.tunit.domain.lesson.dto.LessonFindSummaryDto;
import com.tunit.domain.lesson.dto.LessonSaveDto;
import com.tunit.domain.lesson.dto.LessonStatusRequestDto;
import com.tunit.domain.lesson.service.LessonReserveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonReserveService lessonReserveService;

    @GetMapping("")
    public ResponseEntity<?> getLessonSummary(@LoginUser(field = "tutorProfileNo") Long tutorProfileNo,
                                              @ModelAttribute LessonFindRequestDto lessonFindRequestDto) {
        lessonFindRequestDto.setTutorProfileNo(tutorProfileNo);
        LessonFindSummaryDto lessonSummary = lessonReserveService.getLessonSummary(lessonFindRequestDto);
        log.info("tutors lessonSummary fetched. tutorProfileNo: {}, lessonSummary count: {}", tutorProfileNo, lessonSummary.totalLessonCount());
        return ResponseEntity.ok(lessonSummary);
    }

    @PostMapping("/reserve")
    public ResponseEntity<?> saveLesson(@LoginUser(field = "tutorProfileNo") Long tutorProfileNo,
                                        @RequestBody LessonSaveDto lessonSaveDto) {
        lessonReserveService.saveLesson(tutorProfileNo, lessonSaveDto);
        return ResponseEntity.ok("레슨이 성공적으로 저장되었습니다.");
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

    @DeleteMapping("/{lessonNo}")
    public ResponseEntity<?> deleteLesson(@PathVariable("lessonNo") Long lessonNo) {
        lessonReserveService.deleteLesson(lessonNo);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{lessonNo}/status")
    public ResponseEntity<?> changeLessonStatus(@PathVariable("lessonNo") Long lessonNo, @RequestBody LessonStatusRequestDto lessonStatusRequestDto) {
        lessonReserveService.changeLessonStatus(lessonNo, lessonStatusRequestDto.status());
        return ResponseEntity.noContent().build();
    }

}
