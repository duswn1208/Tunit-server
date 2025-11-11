package com.tunit.domain.contract.controller;

import com.tunit.common.session.annotation.LoginUser;
import com.tunit.domain.contract.define.ContractSource;
import com.tunit.domain.contract.dto.ContractCreateRequestDto;
import com.tunit.domain.contract.dto.ContractResponseDto;
import com.tunit.domain.contract.dto.ContractStatusUpdateRequestDto;
import com.tunit.domain.contract.exception.ContractException;
import com.tunit.domain.contract.service.ContractService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
@Slf4j
public class ContractController {

    private final ContractService contractService;

    /**
     * 계약 생성 (학생이 튜터에게 레슨 계약 요청)
     */
    @PostMapping
    public ResponseEntity<ContractResponseDto> createContract(
            @LoginUser(field = "userNo") Long userNo,
            @RequestBody ContractCreateRequestDto requestDto) {
        log.info("계약 생성 요청 - tutorProfileNo: {}, studentNo: {}, contractType: {}",
                requestDto.getTutorProfileNo(), userNo, requestDto.getContractType());

        requestDto.setStudentNo(userNo);
        requestDto.setSource(ContractSource.STUDENT_REQUEST);
        ContractResponseDto response = contractService.createContract(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 계약 단건 조회
     */
    @GetMapping("/{contractNo}")
    public ResponseEntity<ContractResponseDto> getContract(@PathVariable Long contractNo) {
        log.info("계약 조회 - contractNo: {}", contractNo);

        ContractResponseDto response = contractService.getContract(contractNo);
        return ResponseEntity.ok(response);
    }

    /**
     * 학생의 계약 목록 조회
     */
    @GetMapping("/student")
    public ResponseEntity<List<ContractResponseDto>> getStudentContracts(
            @LoginUser(field = "userNo") Long userNo) {
        log.info("학생 계약 목록 조회 - studentNo: {}", userNo);

        List<ContractResponseDto> response = contractService.getStudentContracts(userNo);
        return ResponseEntity.ok(response);
    }

    /**
     * 튜터의 계약 목록 조회
     */
    @GetMapping("/tutor")
    public ResponseEntity<List<ContractResponseDto>> getTutorContracts(
            @LoginUser(field = "tutorProfileNo") Long tutorProfileNo) {
        log.info("튜터 계약 목록 조회 - tutorProfileNo: {}", tutorProfileNo);

        List<ContractResponseDto> response = contractService.getTutorContracts(tutorProfileNo);
        return ResponseEntity.ok(response);
    }

    /**
     * 계약 상태 변경 (학생)
     * - 결제 확인 요청 (PENDING → CONFIRMING)
     * - 계약 취소 (→ CANCELLED)
     */
    @PatchMapping("/{contractNo}/status/student")
    public ResponseEntity<ContractResponseDto> updateContractStatusByStudent(
            @PathVariable Long contractNo,
            @LoginUser(field = "userNo") Long studentNo,
            @RequestBody ContractStatusUpdateRequestDto requestDto) {
        log.info("계약 상태 변경(학생) - contractNo: {}, studentNo: {}, contractStatus: {}, paymentStatus: {}",
                contractNo, studentNo, requestDto.getContractStatus(), requestDto.getNewPaymentStatus());

        ContractResponseDto response = contractService.updateContractStatus(
                contractNo,
                studentNo,
                requestDto.getContractStatus(),
                requestDto.getCancelReason(),
                false
        );
        return ResponseEntity.ok(response);
    }

    /**
     * 계약 상태 변경 (튜터)
     */
    @PutMapping("/{contractNo}/status/tutor")
    public ResponseEntity<ContractResponseDto> updateContractStatusByTutor(
            @PathVariable Long contractNo,
            @LoginUser(field = "tutorProfileNo") Long tutorProfileNo,
            @RequestBody ContractStatusUpdateRequestDto requestDto) {

        if (requestDto.getContractStatus() == null) {
            throw new ContractException("변경할 계약 상태를 입력해주세요.");
        }

        log.info("계약 상태 변경(튜터) - contractNo: {}, tutorProfileNo: {}, contractStatus: {}, paymentStatus: {}",
                contractNo, tutorProfileNo, requestDto.getContractStatus(), requestDto.getNewPaymentStatus());

        ContractResponseDto response = contractService.updateContractStatus(
                contractNo,
                tutorProfileNo,
                requestDto.getContractStatus(),
                requestDto.getCancelReason(),
                true
        );
        return ResponseEntity.ok(response);
    }
}

