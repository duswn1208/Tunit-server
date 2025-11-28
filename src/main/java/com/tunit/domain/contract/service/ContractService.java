package com.tunit.domain.contract.service;

import com.tunit.domain.contract.define.ContractStatus;
import com.tunit.domain.contract.define.PaymentStatus;
import com.tunit.domain.contract.dto.ContractCreateRequestDto;
import com.tunit.domain.contract.dto.ContractResponseDto;
import com.tunit.domain.contract.entity.StudentTutorContract;
import com.tunit.domain.contract.exception.ContractException;
import com.tunit.domain.lesson.define.ReservationStatus;
import com.tunit.domain.lesson.entity.LessonReservation;
import com.tunit.domain.lesson.service.LessonManagementService;
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
    private final LessonManagementService lessonManagementService;
    private final LessonQueryService lessonQueryService;

    @Transactional
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

        // 추가 레슨 생성 가능 여부 확인
        boolean isReservable = this.canGenerateAdditionalLessons(contractNo);

        ContractResponseDto response = ContractResponseDto.withCurrentLessonCount(contract, activeLessonList.size(), isReservable);
        return response;
    }

    public List<ContractResponseDto> getStudentContracts(Long studentNo) {
        return contractQueryService.getStudentContracts(studentNo);
    }

    public List<ContractResponseDto> getTutorContracts(Long tutorProfileNo) {
        return contractQueryService.getTutorContracts(tutorProfileNo);
    }

    @Transactional
    public ContractResponseDto modifyContract(Long studentNo, Long contractNo, ContractCreateRequestDto requestDto) {
        StudentTutorContract contract = contractQueryService.modifyContract(studentNo, contractNo, requestDto);
        // 대기 레슨 예약 생성
        lessonReserveService.reserveLessonsBatch(contract, requestDto.getLessonDtList());
        return new ContractResponseDto(contract);
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
                lessonManagementService.changeLessonStatusByContractNo(contractNo, ReservationStatus.CANCELED);
            }
            case APPROVED -> {
                contract.updateContractStatus(afterStatus);
                contract.updatePaymentStatus(PaymentStatus.PENDING);
            }
            case ACTIVE -> {
                contract.updateContractStatus(afterStatus);
                lessonManagementService.changeLessonStatusByContractNo(contractNo, ReservationStatus.ACTIVE);
            }
            default -> {
                contract.updateContractStatus(afterStatus);
                lessonManagementService.changeLessonStatusByContractNo(contractNo, ReservationStatus.EXPIRED);
            }
        }

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

    /**
     * 계약의 추가 레슨 생성 가능 여부 확인
     */
    @Transactional(readOnly = true)
    public boolean canGenerateAdditionalLessons(Long contractNo) {
        StudentTutorContract contract = contractQueryService.getContract(contractNo);

        // 기본 단위 레슨 수 계산 (예: 주 3회 × 4주 = 12회)
        int unitCount = contract.getLessonCount() * contract.getWeekCount();
        int lessonCount = lessonQueryService.findByContractNoAndStatusIn(contractNo, ReservationStatus.VALID_LESSON_STATUSES).size();

        // 아직 단위만큼 생성되지 않았으면 추가 생성 가능
        if (lessonCount < unitCount) {
            return true;
        }

        if (lessonCount > 0 && lessonCount % unitCount == 0) {
            return false;
        }

        return true;
    }

    /**
     * 계약의 현재 남은 예약 가능 레슨 수 조회
     */
    @Transactional(readOnly = true)
    public int getRemainingLessonCount(Long contractNo) {
        StudentTutorContract contract = contractQueryService.getContract(contractNo);

        int unitCount = contract.getLessonCount() * contract.getWeekCount();
        int totalCount = lessonQueryService.findByContractNoAndStatusIn(contractNo, ReservationStatus.VALID_LESSON_STATUSES).size();

        // 현재 사이클에서 몇 개가 생성되었는지 계산
        int currentCycleCount = (int) (totalCount % unitCount);
        if (currentCycleCount == 0 && totalCount > 0) {
            currentCycleCount = unitCount;
        }

        return currentCycleCount;
    }


}
