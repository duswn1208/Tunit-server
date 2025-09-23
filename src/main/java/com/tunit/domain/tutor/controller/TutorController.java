package com.tunit.domain.tutor.controller;

import com.tunit.domain.tutor.dto.TutorFindRequestDto;
import com.tunit.domain.tutor.service.TutorProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/tutors")
public class TutorController {

    private final TutorProfileService tutorProfileService;

    @PostMapping("/search")
    public ResponseEntity findTutors(@RequestBody TutorFindRequestDto tutorFindRequestDto) {
        return ResponseEntity.ok(tutorProfileService.findTutors(tutorFindRequestDto));
    }
}
