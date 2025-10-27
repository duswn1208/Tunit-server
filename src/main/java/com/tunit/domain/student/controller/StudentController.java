package com.tunit.domain.student.controller;

import com.tunit.common.session.annotation.LoginUser;
import com.tunit.domain.student.dto.FindMyLessonsRequestDto;
import com.tunit.domain.student.service.StudentService;
import com.tunit.domain.user.dto.StudentProfileSaveDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @ModelAttribute FindMyLessonsRequestDto requestDto) {
        return ResponseEntity.ok(studentService.findMyLessons(userNo, requestDto));
    }
}
