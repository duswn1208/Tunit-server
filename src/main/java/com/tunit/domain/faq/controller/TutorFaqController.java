package com.tunit.domain.faq.controller;

import com.tunit.common.session.annotation.LoginUser;
import com.tunit.domain.faq.dto.TutorFaqCreateRequestDto;
import com.tunit.domain.faq.dto.TutorFaqOrderUpdateRequestDto;
import com.tunit.domain.faq.dto.TutorFaqResponseDto;
import com.tunit.domain.faq.dto.TutorFaqUpdateRequestDto;
import com.tunit.domain.faq.service.TutorFaqService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tutors")
@RequiredArgsConstructor
public class TutorFaqController {

    private final TutorFaqService tutorFaqService;

    // [공개] 특정 튜터의 FAQ 목록 조회
    @GetMapping("/{tutorProfileNo}/faqs")
    public ResponseEntity<List<TutorFaqResponseDto>> getFaqsForUser(@PathVariable Long tutorProfileNo) {
        return ResponseEntity.ok(tutorFaqService.getFaqsForUser(tutorProfileNo));
    }

    // [튜터 전용] 본인의 FAQ 목록 조회
    @GetMapping("/me/faqs")
    public ResponseEntity<List<TutorFaqResponseDto>> getFaqsForTutor(@LoginUser(field = "tutorProfileNo") Long tutorProfileNo) {
        return ResponseEntity.ok(tutorFaqService.getFaqsForTutor(tutorProfileNo));
    }

    // [튜터 전용] FAQ 생성
    @PostMapping("/me/faqs")
    public ResponseEntity<TutorFaqResponseDto> createFaq(
            @LoginUser(field = "tutorProfileNo") Long tutorProfileNo,
            @RequestBody TutorFaqCreateRequestDto requestDto) {
        TutorFaqResponseDto responseDto = tutorFaqService.createFaq(tutorProfileNo, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // [튜터 전용] FAQ 수정
    @PutMapping("/me/faqs/{tutorFaqNo}")
    public ResponseEntity<Void> updateFaq(
            @LoginUser(field = "tutorProfileNo") Long tutorProfileNo,
            @PathVariable Long tutorFaqNo,
            @RequestBody TutorFaqUpdateRequestDto requestDto) {
        tutorFaqService.updateFaq(tutorProfileNo, tutorFaqNo, requestDto);
        return ResponseEntity.ok().build();
    }

    // [튜터 전용] FAQ 삭제
    @DeleteMapping("/me/faqs/{tutorFaqNo}")
    public ResponseEntity<Void> deleteFaq(
            @LoginUser(field = "tutorProfileNo") Long tutorProfileNo,
            @PathVariable Long tutorFaqNo) {
        tutorFaqService.deleteFaq(tutorProfileNo, tutorFaqNo);
        return ResponseEntity.noContent().build();
    }

    // [튜터 전용] FAQ 순서 일괄 변경
    @PutMapping("/me/faqs/order")
    public ResponseEntity<Void> updateFaqOrder(
            @LoginUser(field = "tutorProfileNo") Long tutorProfileNo,
            @RequestBody TutorFaqOrderUpdateRequestDto requestDto) {
        tutorFaqService.updateFaqOrder(tutorProfileNo, requestDto);
        return ResponseEntity.ok().build();
    }
}

