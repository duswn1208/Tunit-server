package com.tunit.domain.tutor.controller;

import com.tunit.common.session.annotation.LoginUser;
import com.tunit.domain.tutor.dto.TutorProfileSaveDto;
import com.tunit.domain.tutor.service.TutorProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/tutor/profile")
@RequiredArgsConstructor
public class TutorProfileController {

    private final TutorProfileService tutorProfileService;

    @PostMapping("/join")
    public ResponseEntity<?> join(@LoginUser(field = "userNo") Long userNo, @RequestBody TutorProfileSaveDto tutorProfileSaveDto, HttpSession session) {
        Long tutorProfileNo = tutorProfileService.save(userNo, tutorProfileSaveDto);
        session.setAttribute("tutorProfileNo", tutorProfileNo); // 세션에 tutorProfileNo 저장
        return ResponseEntity.ok(tutorProfileNo);
    }
//
//    @GetMapping("/me")
//    public ResponseEntity<?> getMyProfileByUserNo(@LoginUser(field = "userNo") Long userNo) {
//        return ResponseEntity.ok(tutorProfileService.findByUserNo(userNo));
//    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(@LoginUser(field = "userNo") Long userNo) {
        return ResponseEntity.ok(tutorProfileService.findTutorProfileInfo(userNo));
    }

}
