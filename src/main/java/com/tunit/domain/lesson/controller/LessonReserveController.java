package com.tunit.domain.lesson.controller;

import com.tunit.common.session.annotation.LoginUser;
import com.tunit.domain.lesson.define.ReservationStatus;
import com.tunit.domain.lesson.dto.LessonReserveSaveDto;
import com.tunit.domain.lesson.dto.LessonStatusRequestDto;
import com.tunit.domain.lesson.service.LessonManagementService;
import com.tunit.domain.lesson.service.LessonReserveProcessorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/lessons")
public class LessonReserveController {
    private final LessonManagementService lessonManagementService;
    private final LessonReserveProcessorService lessonReserveProcessorService;

    /**
     * 레슨 예약 요청
     */
    @PostMapping("/reserve")
    public ResponseEntity<?> reserveLesson(@LoginUser(field = "userNo") Long userNo,
                                           @RequestBody LessonReserveSaveDto lessonReserveSaveDto) {
        lessonReserveProcessorService.processCreate(userNo, lessonReserveSaveDto);
        return ResponseEntity.ok("레슨이 성공적으로 예약되었습니다.");
    }

    /**
     * 레슨 예약 날짜/시간 변경 요청
     */
    @PostMapping("/reschedule/{lessonReservationNo}")
    public ResponseEntity<?> rescheduleLesson(@LoginUser(field = "userNo") Long userNo,
                                              @PathVariable Long lessonReservationNo,
                                              @RequestBody LessonReserveSaveDto dto) {
        lessonReserveProcessorService.processReschedule(userNo, lessonReservationNo, dto);
        return ResponseEntity.ok("레슨 일정이 변경되었습니다.");
    }

    /**
     * 레슨 예약 취소 요청
     */
    @PostMapping("/cancel/{lessonReservationNo}")
    public ResponseEntity<?> cancelLessonReservation(@LoginUser(field = "userNo") Long userNo, @PathVariable("lessonReservationNo") Long lessonReservationNo) {
        lessonReserveProcessorService.processCancel(userNo, lessonReservationNo);
        return ResponseEntity.ok("레슨 예약이 취소되었습니다.");
    }

    @PostMapping("/tutor/create")
    public ResponseEntity<?> createLesson(@LoginUser(field = "tutorProfileNo") Long tutorProfileNo,
                                          @RequestBody LessonReserveSaveDto lessonReserveSaveDto) {
        lessonReserveProcessorService.processTutorCreate(tutorProfileNo, lessonReserveSaveDto);
        return ResponseEntity.ok("레슨이 등록되었습니다.");
    }

    @DeleteMapping("/{lessonNo}")
    public ResponseEntity<?> deleteLesson(@PathVariable("lessonNo") Long lessonNo) {
        lessonManagementService.deleteLesson(lessonNo);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{lessonNo}/status")
    public ResponseEntity<?> changeLessonStatus(@PathVariable("lessonNo") Long lessonNo, @RequestBody LessonStatusRequestDto lessonStatusRequestDto) {
        lessonManagementService.changeLessonStatus(lessonNo, lessonStatusRequestDto.status());
        return ResponseEntity.noContent().build();
    }

}
