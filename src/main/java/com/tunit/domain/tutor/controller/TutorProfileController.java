package com.tunit.domain.tutor.controller;

import com.tunit.common.session.annotation.LoginUser;
import com.tunit.domain.tutor.dto.TutorProfileSaveDto;
import com.tunit.domain.tutor.service.TutorProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tutor")
@RequiredArgsConstructor
public class TutorProfileController {

    private final TutorProfileService tutorProfileService;

    @PostMapping("/join")
    public ResponseEntity<?> join(@LoginUser(field="userNo") Long userNo, @RequestBody TutorProfileSaveDto tutorProfileSaveDto) {
        Long save = tutorProfileService.save(userNo, tutorProfileSaveDto);
        return ResponseEntity.ok(save);
    }

    @GetMapping("/profile/me")
    public ResponseEntity<?> getMyProfile(@LoginUser(field = "tutorProfileNo") Long tutorProfileNo) {
        return ResponseEntity.ok(tutorProfileService.findTutorProfileInfo(tutorProfileNo));
    }

}
