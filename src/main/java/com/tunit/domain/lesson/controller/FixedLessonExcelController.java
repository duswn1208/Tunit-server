package com.tunit.domain.lesson.controller;

import com.tunit.common.session.annotation.LoginUser;
import com.tunit.domain.lesson.dto.FixedLessonUploadResultDto;
import com.tunit.domain.lesson.service.FixedLessonExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/fixed-lessons")
@RequiredArgsConstructor
public class FixedLessonExcelController {
    private final FixedLessonExcelService fixedLessonExcelService;

    @PostMapping("/upload/excel")
    public ResponseEntity<FixedLessonUploadResultDto> uploadExcel(@LoginUser(field = "tutorProfileNo") Long tutorProfileNo, @RequestParam("file") MultipartFile file) {
        FixedLessonUploadResultDto result = fixedLessonExcelService.uploadExcel(tutorProfileNo, file);
        return ResponseEntity.ok(result);
    }
}

