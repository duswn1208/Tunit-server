package com.tunit.domain.lesson.controller;

import com.tunit.common.session.annotation.LoginUser;
import com.tunit.domain.lesson.define.ReservationStatus;
import com.tunit.domain.lesson.dto.LessonReserveSaveDto;
import com.tunit.domain.lesson.dto.LessonStatusRequestDto;
import com.tunit.domain.lesson.service.LessonManagementService;
import com.tunit.domain.lesson.service.LessonReserveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/lessons")
public class LessonReserveController {
    private final LessonReserveService lessonReservationService;
    private final LessonManagementService lessonManagementService;

    /**
     * 레슨 예약 요청
     * @param userNo
     * @param lessonReserveSaveDto
     * @return
     */
    @PostMapping("/reserve")
    public ResponseEntity<?> reserveLesson(@LoginUser(field = "userNo") Long userNo,
                                           @RequestBody LessonReserveSaveDto lessonReserveSaveDto) {
        lessonReservationService.reserveLessonKafka(userNo, lessonReserveSaveDto);
        return ResponseEntity.ok("레슨이 성공적으로 예약 요청되었습니다. 잠시 후 결과를 확인하세요.");
    }

    /**
     * 레슨 예약 날짜/시간 변경 요청
     * @param userNo
     * @param lessonReservationNo
     * @param dto
     * @return
     */
    @PostMapping("/reschedule/{lessonReservationNo}")
    public ResponseEntity<?> rescheduleLesson(@LoginUser(field = "userNo") Long userNo,
                                              @PathVariable Long lessonReservationNo,
                                              @RequestBody LessonReserveSaveDto dto) {
        lessonReservationService.rescheduleLessonKafka(userNo, lessonReservationNo, dto);
        return ResponseEntity.ok("레슨 일정 변경 요청이 접수되었습니다. 잠시 후 결과를 확인하세요.");
    }

    /**
     * 레슨 예약 취소 요청
     * @param userNo
     * @param lessonReservationNo
     * @return
     */
    @PostMapping("/cancel/{lessonReservationNo}")
    public ResponseEntity<?> cancelLessonReservation(@LoginUser(field = "userNo") Long userNo, @PathVariable("lessonReservationNo") Long lessonReservationNo) {
        lessonReservationService.cancelLessonKafka(userNo, lessonReservationNo);
        return ResponseEntity.ok("레슨 예약 취소 요청이 접수되었습니다. 잠시 후 결과를 확인하세요.");
    }

    @PostMapping("/tutor/create")
    public ResponseEntity<?> createLesson(@LoginUser(field = "tutorProfileNo") Long tutorProfileNo,
                                          @RequestBody LessonReserveSaveDto lessonReserveSaveDto) {
        lessonReservationService.createLessonKafka(tutorProfileNo, lessonReserveSaveDto);
        return ResponseEntity.ok("레슨 생성 요청이 접수되었습니다. 잠시 후 결과를 확인하세요.");
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
