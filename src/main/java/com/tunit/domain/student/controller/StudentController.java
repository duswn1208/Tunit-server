package com.tunit.domain.student.controller;

import com.tunit.common.session.annotation.LoginUser;
import com.tunit.domain.student.service.StudentService;
import com.tunit.domain.user.dto.StudentProfileSaveDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
