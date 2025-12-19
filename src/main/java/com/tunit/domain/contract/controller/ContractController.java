package com.tunit.domain.contract.controller;

import com.tunit.common.session.annotation.LoginUser;
import com.tunit.domain.contract.define.ContractSource;
import com.tunit.domain.contract.dto.ContractCreateRequestDto;
import com.tunit.domain.contract.dto.ContractResponseDto;
import com.tunit.domain.contract.dto.ContractStatusUpdateRequestDto;
import com.tunit.domain.contract.exception.ContractException;
import com.tunit.domain.contract.service.ContractService;
import com.tunit.domain.notification.annotation.SendNotification;
import com.tunit.domain.notification.define.NotificationType;
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
    @SendNotification(
            type = NotificationType.CONTRACT_SIGNED,
            title = "새로운 레슨 요청이 도착했습니다.",
            message = "#{#requestDto.generateLessonName()} 요청이 도착했습니다. 확인해주세요.",
            userNoField = "#result.body.tutorProfileNo",
            deepLink = "/tutor/my/students"
    )
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

    @PutMapping("/{contractNo}")
    public ResponseEntity<ContractResponseDto> modifyContract(@LoginUser(field = "userNo") Long userNo, @PathVariable Long contractNo, @RequestBody ContractCreateRequestDto requestDto) {
        log.info("계약 수정 요청 - contractNo: {}, tutorProfileNo: {}, studentNo: {}, contractType: {}",
                contractNo, requestDto.getTutorProfileNo(), userNo, requestDto.getContractType());

        ContractResponseDto contractResponseDto = contractService.modifyContract(userNo, contractNo, requestDto);
        return ResponseEntity.ok(contractResponseDto);
    }

    // 총금액 수정
    @PostMapping("/{contractNo}/amount")
    public ResponseEntity<ContractResponseDto> updateContractAmount(@LoginUser(field = "tutorProfileNo") Long tutorProfileNo,  @PathVariable Long contractNo, @RequestBody Integer newTotalAmount) {
        log.info("계약 총금액 수정 요청 - contractNo: {}, tutorProfileNo: {}, newTotalAmount: {}",
                contractNo, tutorProfileNo, newTotalAmount);

        ContractResponseDto response = contractService.updateContractAmount(contractNo, tutorProfileNo, newTotalAmount);
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
    @SendNotification(
            type = NotificationType.CONTRACT_SIGNED,
            title = "계약 상태가 변경되었습니다.",
            message = "계약 상태가 #{#requestDto.contractStatus.label}(으)로 변경되었습니다.",
            userNoField = "#result.body.tutorProfileNo",
            deepLink = "/student/my/tutors"
    )
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
    @SendNotification(
            type = NotificationType.CONTRACT_SIGNED,
            title = "계약 상태가 변경되었습니다.",
            message = "계약 상태가 #{#requestDto.contractStatus.label}(으)로 변경되었습니다.",
            userNoField = "#result.body.studentNo",
            deepLink = "/student/contracts"
    )
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

