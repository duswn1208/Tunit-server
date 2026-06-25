package com.tunit.domain.lesson.controller;

import com.tunit.common.session.annotation.LoginUser;
import com.tunit.domain.lesson.dto.*;

import com.tunit.domain.lesson.service.LessonLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/lesson-logs")
public class LessonLogController {

    private final LessonLogService lessonLogService;

    /** 튜터: 레슨 일지 작성 */
    @PostMapping
    public ResponseEntity<LessonLogResponseDto> create(
            @LoginUser(field = "tutorProfileNo") Long tutorProfileNo,
            @Valid @RequestBody LessonLogCreateRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonLogService.create(tutorProfileNo, dto));
    }

    /** 튜터: 레슨 일지 수정 */
    @PutMapping("/{logNo}")
    public ResponseEntity<LessonLogResponseDto> update(
            @LoginUser(field = "tutorProfileNo") Long tutorProfileNo,
            @PathVariable Long logNo,
            @Valid @RequestBody LessonLogUpdateRequestDto dto) {
        return ResponseEntity.ok(lessonLogService.update(tutorProfileNo, logNo, dto));
    }

    /** 레슨 일지 단건 조회 (tutor/student 공용) */
    @GetMapping("/lesson/{lessonReservationNo}")
    public ResponseEntity<LessonLogResponseDto> getByLesson(@PathVariable Long lessonReservationNo) {
        return ResponseEntity.ok(lessonLogService.getByLessonReservationNo(lessonReservationNo));
    }

    /** 튜터: 내가 작성한 레슨 일지 목록 */
    @GetMapping("/tutor/my")
    public ResponseEntity<List<LessonLogResponseDto>> getMyLogsAsTutor(
            @LoginUser(field = "tutorProfileNo") Long tutorProfileNo) {
        return ResponseEntity.ok(lessonLogService.getMyLogsAsTutor(tutorProfileNo));
    }

    /** 학생: 내가 받은 레슨 일지 목록 */
    @GetMapping("/student/my")
    public ResponseEntity<List<LessonLogResponseDto>> getMyLogsAsStudent(
            @LoginUser(field = "userNo") Long userNo) {
        return ResponseEntity.ok(lessonLogService.getMyLogsAsStudent(userNo));
    }

    /** 학생: 질문 등록 */
    @PostMapping("/{logNo}/question")
    public ResponseEntity<LessonLogResponseDto> registerQuestion(
            @LoginUser(field = "userNo") Long userNo,
            @PathVariable Long logNo,
            @Valid @RequestBody LessonLogQuestionRequestDto dto) {
        return ResponseEntity.ok(lessonLogService.registerQuestion(userNo, logNo, dto));
    }

    /** 튜터: 계약 내 일지 미작성 완료 레슨 목록 */
    @GetMapping("/unwritten/contract/{contractNo}")
    public ResponseEntity<List<UnwrittenLessonDto>> getUnwrittenLessons(@PathVariable Long contractNo) {
        return ResponseEntity.ok(lessonLogService.getUnwrittenLessons(contractNo));
    }

    /** 튜터: 답변 등록 */
    @PostMapping("/{logNo}/reply")
    public ResponseEntity<LessonLogResponseDto> registerReply(
            @LoginUser(field = "tutorProfileNo") Long tutorProfileNo,
            @PathVariable Long logNo,
            @Valid @RequestBody LessonLogReplyRequestDto dto) {
        return ResponseEntity.ok(lessonLogService.registerReply(tutorProfileNo, logNo, dto));
    }
}
