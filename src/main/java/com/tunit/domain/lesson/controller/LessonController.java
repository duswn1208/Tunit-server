package com.tunit.domain.lesson.controller;

import com.tunit.common.session.annotation.LoginUser;
import com.tunit.domain.lesson.dto.LessonFindRequestDto;
import com.tunit.domain.lesson.dto.LessonFindSummaryDto;
import com.tunit.domain.lesson.service.LessonQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonQueryService lessonQueryService;

    @GetMapping("")
    public ResponseEntity<?> getLessonSummary(@LoginUser(field = "tutorProfileNo") Long tutorProfileNo,
                                              @ModelAttribute LessonFindRequestDto lessonFindRequestDto) {
        lessonFindRequestDto.setTutorProfileNo(tutorProfileNo);
        LessonFindSummaryDto lessonSummary = lessonQueryService.getLessonSummary(lessonFindRequestDto);
        log.info("tutors lessonSummary fetched. tutorProfileNo: {}, lessonSummary count: {}", tutorProfileNo, lessonSummary.totalLessonCount());
        return ResponseEntity.ok(lessonSummary);
    }

    @GetMapping("/exist")
    public ResponseEntity<?> existsLessons(@LoginUser(field = "tutorProfileNo") Long tutorProfileNo) {
        return ResponseEntity.ok(lessonQueryService.existsLessons(tutorProfileNo));
    }

    @GetMapping("/info/{lessonReservationNo}")
    public ResponseEntity<?> getLessonReservationInfo(@PathVariable("lessonReservationNo") Long lessonReservationNo) {
        return ResponseEntity.ok(lessonQueryService.findByLessonReservationNo(lessonReservationNo));
    }

    // 튜터의 레슨일, 이번달 레슨예약일과 시간 조회
    @GetMapping("/schedule/me")
    public ResponseEntity<?> getTutorLessonSchedule(@LoginUser(field = "tutorProfileNo") Long tutorProfileNo,
                                                    @ModelAttribute LessonFindRequestDto lessonFindRequestDto) {
        lessonFindRequestDto.setTutorProfileNo(tutorProfileNo);
        return ResponseEntity.ok(lessonQueryService.getLessonScheduleInfo(lessonFindRequestDto));
    }

    @GetMapping("/tutor/schedule")
    public ResponseEntity<?> getStudentLessonSchedule(@ModelAttribute LessonFindRequestDto lessonFindRequestDto) {
        return ResponseEntity.ok(lessonQueryService.getLessonScheduleInfo(lessonFindRequestDto));
    }
}
