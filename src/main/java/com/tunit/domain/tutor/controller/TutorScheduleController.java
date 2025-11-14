package com.tunit.domain.tutor.controller;

import com.tunit.common.session.annotation.LoginUser;
import com.tunit.domain.tutor.dto.TutorAvailExceptionSaveDto;
import com.tunit.domain.tutor.dto.TutorAvailableTimeUpdateDto;
import com.tunit.domain.tutor.service.TutorHolidayService;
import com.tunit.domain.tutor.service.TutorProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/tutor/schedule")
@RestController
public class TutorScheduleController {

    private final TutorProfileService tutorProfileService;
    private final TutorHolidayService tutorHolidayService;

    @GetMapping("/holiday")
    public ResponseEntity<?> getTutorHoliday(@LoginUser(field = "tutorProfileNo") Long tutorProfileNo) {
        return ResponseEntity.ok(tutorHolidayService.findByTutorProfileNo(tutorProfileNo));
    }

    @PostMapping("/modify/lesson-time")
    public ResponseEntity<?> modifyTutorAvailableTime(@LoginUser(field = "tutorProfileNo") Long tutorProfileNo,
                                                      @RequestBody TutorAvailableTimeUpdateDto tutorAvailableTimeUpdateDto) {
        tutorProfileService.modifyTutorAvailableTime(tutorProfileNo, tutorAvailableTimeUpdateDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/save/holiday")
    public ResponseEntity<?> saveTutorHoliday(@LoginUser(field = "tutorProfileNo") Long tutorProfileNo,
                                              @RequestBody TutorAvailExceptionSaveDto tutorAvailExceptionSaveDto) {
        tutorHolidayService.saveHoliday(tutorProfileNo, tutorAvailExceptionSaveDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/holiday/{tutorHolidayNo}")
    public ResponseEntity<?> deleteTutorHoliday(@LoginUser(field = "tutorProfileNo") Long tutorProfileNo,
                                                @PathVariable Long tutorHolidayNo) {
        tutorHolidayService.deleteHoliday(tutorProfileNo, tutorHolidayNo);
        return ResponseEntity.ok().build();
    }
}
