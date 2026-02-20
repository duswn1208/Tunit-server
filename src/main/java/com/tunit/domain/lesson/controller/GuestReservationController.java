package com.tunit.domain.lesson.controller;

import com.tunit.domain.lesson.dto.GuestReservationRequestDto;
import com.tunit.domain.lesson.dto.GuestReservationResponseDto;
import com.tunit.domain.lesson.service.GuestReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 비회원 예약 공개 API
 * - 로그인 없이 예약 가능
 * - 선생님 프로필 링크를 통한 예약
 * - 마법 링크를 통한 예약 확인/승인
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/public/reservations")
public class GuestReservationController {
    
    private final GuestReservationService guestReservationService;

    /**
     * 비회원 예약 생성 (학생이 선생님 링크로 예약)
     * @param tutorProfileNo 선생님 프로필 번호
     * @param dto 예약 정보 (이름, 전화번호, 날짜, 시간)
     * @return 예약 생성 결과 및 마법 링크
     */
    @PostMapping("/tutors/{tutorProfileNo}")
    public ResponseEntity<?> createGuestReservation(
            @PathVariable Long tutorProfileNo,
            @RequestBody GuestReservationRequestDto dto) {
        
        String magicLink = guestReservationService.createGuestReservation(tutorProfileNo, dto);
        
        return ResponseEntity.ok()
                .body(new GuestReservationCreateResponse(
                        "예약 요청이 완료되었습니다. 선생님 승인 후 확정됩니다.",
                        magicLink
                ));
    }

    /**
     * 마법 링크로 예약 조회
     * @param token JWT 토큰
     * @return 예약 정보
     */
    @GetMapping("/verify/{token}")
    public ResponseEntity<GuestReservationResponseDto> getReservationByToken(
            @PathVariable String token) {
        
        GuestReservationResponseDto reservation = guestReservationService.getReservationByToken(token);
        return ResponseEntity.ok(reservation);
    }

    /**
     * 마법 링크로 예약 승인/거절 (학생용)
     * @param token JWT 토큰
     * @param action "confirm" 또는 "reject"
     * @return 처리 결과
     */
    @PostMapping("/{token}/action")
    public ResponseEntity<?> handleReservationAction(
            @PathVariable String token,
            @RequestParam String action) {
        
        guestReservationService.handleGuestAction(token, action);
        
        String message = action.equals("confirm") 
                ? "예약이 확정되었습니다." 
                : "예약이 취소되었습니다.";
        
        return ResponseEntity.ok(message);
    }

    private record GuestReservationCreateResponse(String message, String magicLink) {}
}
