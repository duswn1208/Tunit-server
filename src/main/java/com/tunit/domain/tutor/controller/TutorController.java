package com.tunit.domain.tutor.controller;

import com.tunit.domain.tutor.service.TutorProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/tutors")
public class TutorController {

    private final TutorProfileService tutorProfileService;

    @GetMapping("/search")
    public void findTutors() {

    }
}
