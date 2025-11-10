package com.tunit.domain.contract.service;

import com.tunit.domain.contract.define.ContractStatus;
import com.tunit.domain.contract.define.PaymentStatus;
import com.tunit.domain.contract.dto.ContractCreateRequestDto;
import com.tunit.domain.contract.dto.ContractResponseDto;
import com.tunit.domain.contract.entity.StudentTutorContract;
import com.tunit.domain.contract.exception.ContractException;
import com.tunit.domain.lesson.define.ReservationStatus;
import com.tunit.domain.lesson.entity.LessonReservation;
import com.tunit.domain.lesson.service.LessonQueryService;
import com.tunit.domain.lesson.service.LessonReserveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ContractService {

    private final ContractQueryService contractQueryService;
    private final LessonReserveService lessonReserveService;
    private final LessonQueryService lessonQueryService;

    public ContractResponseDto createContract(ContractCreateRequestDto requestDto) {
        // 계약 생성
        StudentTutorContract contract = contractQueryService.createContract(requestDto);

        // 대기 레슨 예약 생성
        lessonReserveService.reserveLessonsBatch(contract, requestDto.getLessonDtList());

        return new ContractResponseDto(contract);
    }

    public ContractResponseDto getContract(Long contractNo) {
        StudentTutorContract contract = contractQueryService.getContract(contractNo);

        List<LessonReservation> activeLessonList = lessonQueryService.findByContractNoAndStatusIn(contractNo, ReservationStatus.VALID_LESSON_STATUSES);
        ContractResponseDto response = ContractResponseDto.withCurrentLessonCount(contract, activeLessonList.size());
        return response;
    }

    public List<ContractResponseDto> getStudentContracts(Long studentNo) {
        return contractQueryService.getStudentContracts(studentNo);
    }

    public List<ContractResponseDto> getTutorContracts(Long tutorProfileNo) {
        return contractQueryService.getTutorContracts(tutorProfileNo);
    }

    // ==================== 계약 상태 변경 ====================

    /**
     * 계약 상태 변경 (ContractStatus 변경)
     */
    @Transactional
    public ContractResponseDto updateContractStatus(Long contractNo, Long userNo,
                                                    ContractStatus afterStatus,
                                                    String cancelReason,
                                                    boolean isTutor) {
        StudentTutorContract contract = contractQueryService.getContract(contractNo);

        // 본인 계약인지 확인
        validateContractOwnership(contract, userNo, isTutor);

        if (afterStatus == null) {
            throw new ContractException();
        }

        // 계약 및 레슨 상태 변경
        contract.getContractStatus().changeableTo(afterStatus);
        switch (afterStatus) {
            case CANCELLED -> {
                contract.cancelContract(cancelReason, userNo);
                lessonReserveService.changeLessonStatusByContractNo(contractNo, ReservationStatus.CANCELED);
            }
            case APPROVED -> {
                contract.updateContractStatus(afterStatus);
                contract.updatePaymentStatus(PaymentStatus.PENDING);
            }
            case ACTIVE -> {
                contract.updateContractStatus(afterStatus);
                lessonReserveService.changeLessonStatusByContractNo(contractNo, ReservationStatus.ACTIVE);
            }
            default -> {
                contract.updateContractStatus(afterStatus);
                lessonReserveService.changeLessonStatusByContractNo(contractNo, ReservationStatus.EXPIRED);
            }
        }

        return new ContractResponseDto(contract);
    }

    // ==================== 결제 상태 변경 ====================

    /**
     * 결제 요청 후 입금 확인 중으로 상태 변경
     * PaymentRequest 생성 후 호출하여 Contract의 결제 상태를 CONFIRMING으로 변경
     */
    @Transactional
    public ContractResponseDto updateToConfirming(Long contractNo, Long studentNo) {
        StudentTutorContract contract = contractQueryService.getContract(contractNo);

        // 본인 계약인지 확인 (학생)
        validateStudentOwnership(contract, studentNo);

        // 결제 대기 상태인지 확인
        if (contract.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new ContractException("결제 대기 상태에서만 입금 확인 요청이 가능합니다.");
        }

        // 결제 상태를 CONFIRMING으로 변경
        contract.updatePaymentStatus(PaymentStatus.CONFIRMING);

        return new ContractResponseDto(contract);
    }

    /**
     * 결제 승인 (튜터가 입금 확인 후 호출)
     * PaymentRequest가 CONFIRMED된 후 호출하여 Contract의 결제 상태를 COMPLETED로 변경
     */
    @Transactional
    public ContractResponseDto updateToCompleted(Long contractNo, Long tutorProfileNo, Integer paidAmount) {
        StudentTutorContract contract = contractQueryService.getContract(contractNo);

        // 본인 계약인지 확인
        if (!contract.getTutorProfileNo().equals(tutorProfileNo)) {
            throw new ContractException("본인의 계약만 수정할 수 있습니다.");
        }

        // 입금 확인 중 상태인지 확인
        if (contract.getPaymentStatus() != PaymentStatus.CONFIRMING) {
            throw new ContractException("입금 확인 중 상태에서만 결제 완료 처리가 가능합니다.");
        }

        // 결제 승인 및 계약 활성화
        contract.approvePayment(paidAmount);
        lessonReserveService.changeLessonStatusByContractNo(contractNo, ReservationStatus.ACTIVE);

        return new ContractResponseDto(contract);
    }

    private void validateContractOwnership(StudentTutorContract contract, Long userNo, boolean isTutor) {
        if (isTutor) {
            if (!contract.getTutorProfileNo().equals(userNo)) {
                throw new ContractException("본인의 계약만 수정할 수 있습니다.");
            }
        } else {
            if (!contract.getStudentNo().equals(userNo)) {
                throw new ContractException("본인의 계약만 수정할 수 있습니다.");
            }
        }
    }

    private void validateStudentOwnership(StudentTutorContract contract, Long studentNo) {
        if (!contract.getStudentNo().equals(studentNo)) {
            throw new ContractException("본인의 계약만 수정할 수 있습니다.");
        }
    }
}
