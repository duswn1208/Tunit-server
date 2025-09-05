package com.tunit.domain.lesson.controller;

import com.tunit.common.session.annotation.LoginUser;
import com.tunit.domain.lesson.dto.FixedLessonSaveDto;
import com.tunit.domain.lesson.dto.FixedLessonUploadResultDto;
import com.tunit.domain.lesson.service.FixedLessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/fixed-lessons")
@RequiredArgsConstructor
public class FixedLessonExcelController {
    private final FixedLessonService fixedLessonService;

    @PostMapping("/upload/excel")
    public ResponseEntity<FixedLessonUploadResultDto> uploadExcel(@LoginUser(field = "tutorProfileNo") Long tutorProfileNo, @RequestParam("file") MultipartFile file) {
        FixedLessonUploadResultDto result = fixedLessonService.uploadExcel(tutorProfileNo, file);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/save" )
    public ResponseEntity<?> saveFixedLesson(@LoginUser(field = "tutorProfileNo") Long tutorProfileNo, @RequestBody FixedLessonSaveDto fixedLessonSaveDto) {
        fixedLessonService.saveFixedLesson(tutorProfileNo, fixedLessonSaveDto);
        return ResponseEntity.ok("성공적으로 저장했습니다.");
    }
}

