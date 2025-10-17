package com.tunit.domain.student.controller;

import com.tunit.common.session.annotation.LoginUser;
import com.tunit.domain.student.service.StudentService;
import com.tunit.domain.user.dto.StudentProfileSaveDto;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping("/profile/join")
    public ResponseEntity<?> studentJoin(@LoginUser(field = "userNo") Long userNo, @RequestBody StudentProfileSaveDto studentProfileSaveDto) {
        studentProfileSaveDto.setUserNo(userNo);
        studentService.joinStudentProfile(studentProfileSaveDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/lessons/my")
    public ResponseEntity<?> getMyLessons(
            @LoginUser(field = "userNo") Long userNo,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return ResponseEntity.ok(studentService.findMyLessons(userNo, startDate, endDate));
    }
}
