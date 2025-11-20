package com.tunit.domain.tutor.controller;

import com.tunit.domain.tutor.dto.TutorFindRequestDto;
import com.tunit.domain.tutor.dto.TutorProfileDetailInfo;
import com.tunit.domain.tutor.service.TutorProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/tutors")
public class TutorController {

    private final TutorProfileService tutorProfileService;

    @PostMapping("/search")
    public ResponseEntity<List<TutorProfileDetailInfo>> findTutors(@RequestBody TutorFindRequestDto tutorFindRequestDto) {
        return ResponseEntity.ok(tutorProfileService.findTutors(tutorFindRequestDto));
    }

    @GetMapping("/{tutorProfileNo}")
    public ResponseEntity getTutorProfile(@PathVariable Long tutorProfileNo) {
        return ResponseEntity.ok(tutorProfileService.findTutorDetailInfo(tutorProfileNo));
    }
}
