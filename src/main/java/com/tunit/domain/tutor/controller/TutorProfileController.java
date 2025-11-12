package com.tunit.domain.tutor.controller;

import com.tunit.common.session.annotation.LoginUser;
import com.tunit.common.session.dto.SessionUser;
import com.tunit.domain.tutor.dto.TutorAvailableTimeUpdateDto;
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
        SessionUser loginUser = (SessionUser) session.getAttribute("LOGIN_USER");
        SessionUser newSessionUser = loginUser.updateTutorProfileNo(tutorProfileNo);
        session.setAttribute("LOGIN_USER", newSessionUser);
        return ResponseEntity.ok(tutorProfileNo);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(@LoginUser(field = "userNo") Long userNo) {
        return ResponseEntity.ok(tutorProfileService.findTutorProfileInfo(userNo));
    }

}
