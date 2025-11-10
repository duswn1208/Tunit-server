package com.tunit.domain.lesson.controller;

import com.tunit.common.session.annotation.LoginUser;
import com.tunit.domain.lesson.define.LessonCategory;
import com.tunit.domain.lesson.define.LessonSubCategory;
import com.tunit.domain.lesson.define.ReservationStatus;
import com.tunit.domain.lesson.dto.LessonFindRequestDto;
import com.tunit.domain.lesson.dto.LessonFindSummaryDto;
import com.tunit.domain.lesson.dto.LessonReserveSaveDto;
import com.tunit.domain.lesson.dto.LessonStatusRequestDto;
import com.tunit.domain.lesson.service.LessonReserveService;
import com.tunit.domain.lesson.service.LessonService;
import com.tunit.domain.tutor.dto.TutorLessonsResponseDto;
import com.tunit.domain.tutor.service.TutorProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonReserveService lessonReserveService;
    private final LessonService lessonService;
    private final TutorProfileService tutorProfileService;

    @GetMapping("")
    public ResponseEntity<?> getLessonSummary(@LoginUser(field = "tutorProfileNo") Long tutorProfileNo,
                                              @ModelAttribute LessonFindRequestDto lessonFindRequestDto) {
        lessonFindRequestDto.setTutorProfileNo(tutorProfileNo);
        LessonFindSummaryDto lessonSummary = lessonService.getLessonSummary(lessonFindRequestDto);
        log.info("tutors lessonSummary fetched. tutorProfileNo: {}, lessonSummary count: {}", tutorProfileNo, lessonSummary.totalLessonCount());
        return ResponseEntity.ok(lessonSummary);
    }

    @PostMapping("/reserve")
    public ResponseEntity<?> reserveLesson(@LoginUser(field = "userNo") Long userNo,
                                           @RequestBody LessonReserveSaveDto lessonReserveSaveDto) {
        lessonReserveService.reserveLesson(userNo, lessonReserveSaveDto);
        return ResponseEntity.ok("레슨이 성공적으로 예약되었습니다.");
    }

    @PostMapping("/reschedule/{lessonReservationNo}")
    public ResponseEntity<?> rescheduleLessonReservation(@LoginUser(field = "userNo") Long userNo,
                                                         @PathVariable ("lessonReservationNo") Long lessonReservationNo,
                                                         @RequestBody LessonReserveSaveDto lessonReserveSaveDto) {
        lessonReserveService.reschedule(userNo, lessonReservationNo, lessonReserveSaveDto);
        return ResponseEntity.ok("레슨 예약이 성공적으로 변경되었습니다.");
    }

    @PostMapping("/cancel/{lessonReservationNo}")
    public ResponseEntity<?> cancelLessonReservation(@LoginUser(field = "userNo") Long userNo, @PathVariable ("lessonReservationNo") Long lessonReservationNo) {
        lessonReserveService.cancel(userNo, lessonReservationNo, ReservationStatus.CANCELED);
        return ResponseEntity.ok("레슨 예약이 성공적으로 취소되었습니다.");
    }

    @PostMapping("/tutor/create")
    public ResponseEntity<?> createLesson(@LoginUser(field = "tutorProfileNo") Long tutorProfileNo,
                                          @RequestBody LessonReserveSaveDto lessonReserveSaveDto) {
        lessonReserveService.createLesson(tutorProfileNo, lessonReserveSaveDto);
        return ResponseEntity.ok("레슨이 성공적으로 저장되었습니다.");
    }

    //todo: cache 적용 필요
    @GetMapping("/categories")
    @ResponseBody
    public ResponseEntity<List<LessonCategory>> getLessonCategories() {
        return ResponseEntity.ok(List.of(LessonCategory.values()));
    }

    @GetMapping("/tutor/categories")
    public ResponseEntity<List<TutorLessonsResponseDto>> getTutorLessonCategories(@LoginUser(field = "tutorProfileNo") Long tutorProfileNo) {
        return ResponseEntity.ok(tutorProfileService.findTutorLessonsByTutorProfileNo(tutorProfileNo));
    }

    @GetMapping("/categories/{code}/subcategories")
    @ResponseBody
    public ResponseEntity<List<LessonSubCategory>> getLessonSubcategories(@PathVariable("code") String code) {
        return ResponseEntity.ok(LessonCategory.fromCode(code).getSubCategories());
    }

    @GetMapping("/exist")
    public ResponseEntity<?> existsLessons(@LoginUser(field = "tutorProfileNo") Long tutorProfileNo) {
        return ResponseEntity.ok(lessonService.existsLessons(tutorProfileNo));
    }

    @DeleteMapping("/{lessonNo}")
    public ResponseEntity<?> deleteLesson(@PathVariable("lessonNo") Long lessonNo) {
        lessonReserveService.deleteLesson(lessonNo);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{lessonNo}/status")
    public ResponseEntity<?> changeLessonStatus(@PathVariable("lessonNo") Long lessonNo, @RequestBody LessonStatusRequestDto lessonStatusRequestDto) {
        lessonReserveService.changeLessonStatus(lessonNo, lessonStatusRequestDto.status());
        return ResponseEntity.noContent().build();
    }

    // 튜터의 레슨일, 이번달 레슨예약일과 시간 조회
    @GetMapping("/schedule/me")
    public ResponseEntity<?> getTutorLessonSchedule(@LoginUser(field = "tutorProfileNo") Long tutorProfileNo,
                                                  @ModelAttribute LessonFindRequestDto lessonFindRequestDto) {
        lessonFindRequestDto.setTutorProfileNo(tutorProfileNo);
        return ResponseEntity.ok(lessonService.getLessonScheduleInfo(lessonFindRequestDto));
    }

    @GetMapping("/tutor/schedule")
    public ResponseEntity<?> getStudentLessonSchedule(@ModelAttribute LessonFindRequestDto lessonFindRequestDto) {
        return ResponseEntity.ok(lessonService.getLessonScheduleInfo(lessonFindRequestDto));
    }

    @GetMapping("/info/{lessonReservationNo}")
    public ResponseEntity<?> getLessonReservationInfo(@PathVariable("lessonReservationNo") Long lessonReservationNo) {
        return ResponseEntity.ok(lessonService.findByLessonReservationNo(lessonReservationNo));
    }
}
