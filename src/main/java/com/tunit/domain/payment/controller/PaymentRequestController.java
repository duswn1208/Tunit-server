package com.tunit.domain.payment.controller;

import com.tunit.common.session.annotation.LoginUser;
import com.tunit.domain.payment.dto.PaymentRejectRequestDto;
import com.tunit.domain.payment.dto.PaymentRequestCreateDto;
import com.tunit.domain.payment.dto.PaymentRequestResponseDto;
import com.tunit.domain.payment.service.PaymentRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contracts/payment-requests")
@RequiredArgsConstructor
@Slf4j
public class PaymentRequestController {

    private final PaymentRequestService paymentRequestService;

    /**
     * 결제 확인 요청 생성 (학생)
     */
    @PostMapping
    public ResponseEntity<PaymentRequestResponseDto> createPaymentRequest(
            @LoginUser(field = "userNo") Long studentNo,
            @RequestBody PaymentRequestCreateDto requestDto) {
        log.info("결제 확인 요청 생성 - studentNo: {}, contractNo: {}, amount: {}",
                studentNo, requestDto.getContractNo(), requestDto.getPaymentAmount());

        PaymentRequestResponseDto response = paymentRequestService.createPaymentRequest(studentNo, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 결제 확인 (튜터)
     */
    @PostMapping("/{paymentNo}/confirm")
    public ResponseEntity<PaymentRequestResponseDto> confirmPayment(
            @PathVariable Long paymentNo,
            @LoginUser(field = "tutorProfileNo") Long tutorProfileNo) {
        log.info("결제 확인 - paymentNo: {}, tutorProfileNo: {}", paymentNo, tutorProfileNo);

        PaymentRequestResponseDto response = paymentRequestService.confirmPayment(paymentNo, tutorProfileNo);
        return ResponseEntity.ok(response);
    }

    /**
     * 결제 거절 (튜터)
     */
    @PostMapping("/{paymentNo}/reject")
    public ResponseEntity<PaymentRequestResponseDto> rejectPayment(
            @PathVariable Long paymentNo,
            @LoginUser(field = "tutorProfileNo") Long tutorProfileNo,
            @RequestBody PaymentRejectRequestDto requestDto) {
        log.info("결제 거절 - paymentNo: {}, tutorProfileNo: {}, reason: {}",
                paymentNo, tutorProfileNo, requestDto.getRejectReason());

        PaymentRequestResponseDto response = paymentRequestService.rejectPayment(
                paymentNo, tutorProfileNo, requestDto.getRejectReason());
        return ResponseEntity.ok(response);
    }

    /**
     * 결제 요청 단건 조회
     */
    @GetMapping("/{paymentNo}")
    public ResponseEntity<PaymentRequestResponseDto> getPaymentRequest(@PathVariable Long paymentNo) {
        log.info("결제 요청 조회 - paymentNo: {}", paymentNo);

        PaymentRequestResponseDto response = paymentRequestService.getPaymentRequest(paymentNo);
        return ResponseEntity.ok(response);
    }

    /**
     * 계약별 결제 요청 목록 조회
     */
    @GetMapping("/contract/{contractNo}")
    public ResponseEntity<List<PaymentRequestResponseDto>> getPaymentRequestsByContract(
            @PathVariable Long contractNo) {
        log.info("계약별 결제 요청 목록 조회 - contractNo: {}", contractNo);

        List<PaymentRequestResponseDto> response = paymentRequestService.getPaymentRequestsByContract(contractNo);
        return ResponseEntity.ok(response);
    }

    /**
     * 학생의 결제 요청 목록 조회
     */
    @GetMapping("/student")
    public ResponseEntity<List<PaymentRequestResponseDto>> getPaymentRequestsByStudent(
            @LoginUser(field = "userNo") Long studentNo) {
        log.info("학생 결제 요청 목록 조회 - studentNo: {}", studentNo);

        List<PaymentRequestResponseDto> response = paymentRequestService.getPaymentRequestsByStudent(studentNo);
        return ResponseEntity.ok(response);
    }

    /**
     * 튜터의 결제 요청 목록 조회
     */
    @GetMapping("/tutor")
    public ResponseEntity<List<PaymentRequestResponseDto>> getPaymentRequestsByTutor(
            @LoginUser(field = "tutorProfileNo") Long tutorProfileNo) {
        log.info("튜터 결제 요청 목록 조회 - tutorProfileNo: {}", tutorProfileNo);

        List<PaymentRequestResponseDto> response = paymentRequestService.getPaymentRequestsByTutor(tutorProfileNo);
        return ResponseEntity.ok(response);
    }
}
