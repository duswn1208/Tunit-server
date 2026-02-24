package com.tunit.domain.contract.controller;

import com.tunit.common.session.annotation.LoginUser;
import com.tunit.domain.contract.define.ContractSource;
import com.tunit.domain.contract.dto.ContractCreateRequestDto;
import com.tunit.domain.contract.dto.ContractResponseDto;
import com.tunit.domain.contract.dto.ContractStatusUpdateRequestDto;
import com.tunit.domain.contract.dto.TrialConfirmDto;
import com.tunit.domain.contract.dto.TrialRejectDto;
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

    // ==================== 체험 레슨 관련 ====================

    /**
     * 체험 레슨 계약 생성 (학생)
     */
    @PostMapping("/trial")
    @SendNotification(
        type = NotificationType.CONTRACT_SIGNED,
        title = "새로운 체험 레슨 요청",
        message = "#{#requestDto.generateLessonName()} 요청이 도착했습니다.",
        userNoField = "#result.body.tutorProfileNo",
        deepLink = "/tutor/my/students"
    )
    public ResponseEntity<ContractResponseDto> createTrialContract(
        @LoginUser(field = "userNo") Long studentNo,
        @RequestBody ContractCreateRequestDto requestDto
    ) {
        log.info("체험 레슨 계약 생성 - studentNo: {}, tutorProfileNo: {}", 
            studentNo, requestDto.getTutorProfileNo());
        
        requestDto.setStudentNo(studentNo);
        requestDto.setSource(ContractSource.STUDENT_REQUEST);
        
        ContractResponseDto response = contractService.createTrialContract(requestDto);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 체험 레슨 시간 확정 (튜터)
     */
    @PostMapping("/{contractNo}/trial/confirm")
    @SendNotification(
        type = NotificationType.CONTRACT_SIGNED,
        title = "체험 레슨이 확정되었습니다",
        message = "#{#dto.selectedDate} #{#dto.selectedStartTime} 레슨이 확정되었습니다",
        userNoField = "#result.body.studentNo",
        deepLink = "/student/contracts"
    )
    public ResponseEntity<ContractResponseDto> confirmTrialContract(
        @PathVariable Long contractNo,
        @LoginUser(field = "tutorProfileNo") Long tutorProfileNo,
        @RequestBody TrialConfirmDto dto
    ) {
        log.info("체험 레슨 확정 - contractNo: {}, tutorProfileNo: {}", 
            contractNo, tutorProfileNo);
        
        ContractResponseDto response = contractService.confirmTrialContract(
            contractNo,
            tutorProfileNo,
            dto
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * 체험 레슨 거절 (튜터)
     * - reason만 제공: 단순 거절
     * - alternativeTimes 제공: 대안 시간 제안
     */
    @PostMapping("/{contractNo}/trial/reject")
    public ResponseEntity<ContractResponseDto> rejectTrialContract(
        @PathVariable Long contractNo,
        @LoginUser(field = "tutorProfileNo") Long tutorProfileNo,
        @RequestBody TrialRejectDto dto
    ) {
        log.info("체험 레슨 거절 - contractNo: {}, hasAlternatives: {}", 
            contractNo, dto.hasAlternatives());
        
        ContractResponseDto response = contractService.rejectTrialContract(
            contractNo,
            tutorProfileNo,
            dto
        );
        
        return ResponseEntity.ok(response);
    }

    /**
     * 학생이 튜터 제안 시간 수락
     */
    @PostMapping("/{contractNo}/trial/accept-proposal/{proposalId}")
    @SendNotification(
        type = NotificationType.CONTRACT_SIGNED,
        title = "체험 레슨이 확정되었습니다",
        message = "학생이 제안 시간을 수락했습니다",
        userNoField = "#result.body.tutorProfileNo",
        deepLink = "/tutor/contracts"
    )
    public ResponseEntity<ContractResponseDto> acceptTutorProposal(
        @PathVariable Long contractNo,
        @PathVariable Long proposalId,
        @LoginUser(field = "userNo") Long studentNo
    ) {
        log.info("튜터 제안 시간 수락 - contractNo: {}, proposalId: {}", 
            contractNo, proposalId);
        
        ContractResponseDto response = contractService.acceptTutorProposal(
            contractNo,
            studentNo,
            proposalId
        );
        
        return ResponseEntity.ok(response);
    }
}

