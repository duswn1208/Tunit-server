package com.tunit.domain.contract.service;

import com.tunit.domain.contract.define.ContractStatus;
import com.tunit.domain.contract.define.PaymentStatus;
import com.tunit.domain.contract.dto.ContractCreateRequestDto;
import com.tunit.domain.contract.dto.ContractResponseDto;
import com.tunit.domain.contract.dto.TrialConfirmDto;
import com.tunit.domain.contract.dto.TrialRejectDto;
import com.tunit.domain.contract.entity.StudentTutorContract;
import com.tunit.domain.contract.entity.TrialContractCandidate;
import com.tunit.domain.contract.entity.TrialContractProposal;
import com.tunit.domain.contract.exception.ContractException;
import com.tunit.domain.contract.repository.TrialContractCandidateRepository;
import com.tunit.domain.contract.repository.TrialContractProposalRepository;
import com.tunit.domain.lesson.define.ReservationStatus;
import com.tunit.domain.lesson.entity.LessonReservation;
import com.tunit.domain.lesson.service.LessonManagementService;
import com.tunit.domain.lesson.service.LessonQueryService;
import com.tunit.domain.lesson.service.LessonReserveProcessorService;
import com.tunit.domain.notification.service.NotificationEventService;
import com.tunit.domain.tutor.entity.TutorProfile;
import com.tunit.domain.tutor.service.TutorAvailableTimeService;
import com.tunit.domain.tutor.service.TutorProfileService;
import com.tunit.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContractService {

    private final ContractQueryService contractQueryService;
    private final LessonReserveProcessorService lessonReserveService;
    private final LessonManagementService lessonManagementService;
    private final LessonQueryService lessonQueryService;
    private final TutorProfileService tutorProfileService;
    private final UserService userService;
    private final NotificationEventService notificationEventService;
    private final TrialContractCandidateRepository trialCandidateRepository;
    private final TrialContractProposalRepository trialProposalRepository;
    private final TutorAvailableTimeService tutorAvailableTimeService;

    @Transactional
    public ContractResponseDto createContract(ContractCreateRequestDto requestDto) {
        // 계약 생성
        StudentTutorContract contract = contractQueryService.createContract(requestDto, tutorProfileService.findDurationMinByTutorProfileNo(requestDto.getTutorProfileNo()));

        // 대기 레슨 예약 생성
        lessonReserveService.reserveLessonsBatch(contract, requestDto.getLessonDtList());

        return withTutorUserNo(ContractResponseDto.fromEntity(contract));
    }

    /**
     * 알림 발송 대상이 튜터인 응답에 튜터의 userNo를 채워준다.
     * (tutorProfileNo 는 프로필 PK 이므로 @SendNotification 의 userNo 로 사용할 수 없음)
     */
    private ContractResponseDto withTutorUserNo(ContractResponseDto response) {
        response.setTutorUserNo(tutorProfileService.findByTutorProfileNo(response.getTutorProfileNo()).getUserNo());
        return response;
    }

    public ContractResponseDto getContract(Long contractNo) {
        StudentTutorContract contract = contractQueryService.getContract(contractNo);

        List<LessonReservation> activeLessonList = lessonQueryService.findByContractNoAndStatusIn(contractNo, ReservationStatus.VALID_LESSON_STATUSES);

        // 추가 레슨 생성 가능 여부 확인
        boolean isReservable = this.canGenerateAdditionalLessons(contractNo);

        ContractResponseDto response = ContractResponseDto.withCurrentLessonCount(contract, activeLessonList.size(), isReservable);

        // 계약 승인 후 미결제 상태일 때 계좌 정보 포함
        if (contract.getContractStatus() == ContractStatus.APPROVED
                && (contract.getPaymentStatus() == PaymentStatus.PENDING
                    || contract.getPaymentStatus() == PaymentStatus.CONFIRMING)) {
            TutorProfile tutorProfile = tutorProfileService.findByTutorProfileNo(contract.getTutorProfileNo());
            response.setTutorBankName(tutorProfile.getBankName());
            response.setTutorAccountNumber(tutorProfile.getAccountNumber());
            response.setTutorAccountHolder(tutorProfile.getAccountHolder());
        }

        return response;
    }

    public List<ContractResponseDto> getStudentContracts(Long studentNo) {
        return contractQueryService.getStudentContracts(studentNo);
    }

    @Transactional
    public List<ContractResponseDto> getTutorContracts(Long tutorProfileNo) {
        // 1. 시간 선택 전인 체험 계약들의 후보 시간 가능 여부 재평가
        //    (가능 시간대 설정이 바뀌었거나, 과거 버그로 저장된 stale 값을 보정)
        refreshPendingTrialAvailability(tutorProfileNo);

        // 2. 응답 조회
        List<ContractResponseDto> tutorContracts = contractQueryService.getTutorContracts(tutorProfileNo);
        tutorContracts.forEach(contract -> {
            contract.setStudentName(userService.findByUserNo(contract.getStudentNo()).getName());
        });
        return tutorContracts;
    }

    private void refreshPendingTrialAvailability(Long tutorProfileNo) {
        List<StudentTutorContract> pending = contractQueryService
                .findPendingTrialContractsByTutor(tutorProfileNo);
        for (StudentTutorContract contract : pending) {
            List<TrialContractCandidate> candidates = trialCandidateRepository
                    .findByContract_ContractNoOrderByPriority(contract.getContractNo());
            checkAndUpdateCandidateAvailability(tutorProfileNo, candidates);
        }
    }

    @Transactional
    public ContractResponseDto modifyContract(Long studentNo, Long contractNo, ContractCreateRequestDto requestDto) {
        StudentTutorContract contract = contractQueryService.modifyContract(studentNo, contractNo, requestDto);
        // 대기 레슨 예약 생성
        lessonReserveService.reserveLessonsBatch(contract, requestDto.getLessonDtList());
        return ContractResponseDto.fromEntity(contract);
    }

    @Transactional
    public ContractResponseDto updateContractAmount(Long contractNo, Long tutorProfileNo, Integer newTotalAmount) {
        StudentTutorContract contract = contractQueryService.getContract(contractNo);
        if (!contract.getTutorProfileNo().equals(tutorProfileNo)) {
            throw new ContractException("본인의 계약만 수정할 수 있습니다.");
        }
        contract.updateTotalAmount(newTotalAmount);
        return ContractResponseDto.fromEntity(contract);
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
                TutorProfile tutorProfile = tutorProfileService.findByTutorProfileNo(contract.getTutorProfileNo());
                if (tutorProfile.getBankName() != null && tutorProfile.getAccountNumber() != null) {
                    String tutorName = userService.findByUserNo(tutorProfile.getUserNo()).getName();
                    notificationEventService.sendPaymentAccountNotification(
                            contract.getStudentNo(),
                            contract.getContractNo(),
                            tutorName,
                            contract.getTotalPrice(),
                            tutorProfile.getBankName(),
                            tutorProfile.getAccountNumber(),
                            tutorProfile.getAccountHolder()
                    );
                } else {
                    log.warn("튜터 계좌 미등록 - tutorProfileNo: {}, contractNo: {}", contract.getTutorProfileNo(), contractNo);
                }
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

        return withTutorUserNo(ContractResponseDto.fromEntity(contract));
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

    // ==================== 체험 레슨 관련 ====================

    /**
     * 체험 레슨 계약 생성
     */
    @Transactional
    public ContractResponseDto createTrialContract(ContractCreateRequestDto dto) {
        log.info("체험 레슨 계약 생성 시작 - tutorProfileNo: {}, studentNo: {}",
                dto.getTutorProfileNo(), dto.getStudentNo());

        // 1. 후보 시간 검증
        validateTrialCandidates(dto.getTrialCandidates());

        // 2. Contract 생성
        StudentTutorContract contract = contractQueryService.createContract(
                dto,
                tutorProfileService.findDurationMinByTutorProfileNo(dto.getTutorProfileNo())
        );

        // 3. 후보 시간들 저장
        List<TrialContractCandidate> candidates = saveTrialCandidates(
                contract,
                dto.getTrialCandidates()
        );

        // 4. 각 후보 시간의 가용성 체크
        checkAndUpdateCandidateAvailability(dto.getTutorProfileNo(), candidates);

        log.info("체험 레슨 계약 생성 완료 - contractNo: {}", contract.getContractNo());

        return withTutorUserNo(ContractResponseDto.fromEntity(contract));
    }

    /**
     * 체험 레슨 시간 확정 (튜터가 후보 중 선택)
     */
    @Transactional
    public ContractResponseDto confirmTrialContract(
            Long contractNo,
            Long tutorProfileNo,
            TrialConfirmDto dto
    ) {
        log.info("체험 레슨 확정 - contractNo: {}", contractNo);

        // 1. Contract 조회
        StudentTutorContract contract = contractQueryService.getContract(contractNo);

        // 2. 권한 체크
        if (!contract.getTutorProfileNo().equals(tutorProfileNo)) {
            throw new ContractException("권한이 없습니다");
        }

        // 3. 타입 및 상태 체크
        if (!contract.isTrial()) {
            throw new ContractException("체험 레슨이 아닙니다");
        }

        // REQUESTED 또는 (APPROVED 이면서 아직 시간 미확정)일 때만 허용
        ContractStatus status = contract.getContractStatus();
        boolean isPendingSelection =
            status == ContractStatus.REQUESTED
                || (status == ContractStatus.APPROVED
                    && contract.getSelectedCandidateDate() == null);
        if (!isPendingSelection) {
            throw new ContractException("이미 처리된 계약입니다");
        }

        // 4. 후보 시간 조회
        List<TrialContractCandidate> candidates = trialCandidateRepository
                .findByContract_ContractNoOrderByPriority(contractNo);

        if (candidates.isEmpty()) {
            throw new ContractException("후보 시간이 없습니다");
        }

        // 5. 선택한 시간 검증
        TrialContractCandidate selected = candidates.stream()
                .filter(c ->
                        c.getCandidateDate().equals(dto.getSelectedDate()) &&
                                c.getCandidateStartTime().equals(dto.getSelectedStartTime())
                )
                .findFirst()
                .orElseThrow(() -> new ContractException("유효하지 않은 시간입니다"));

        // 가능 여부를 fresh 하게 재계산 (저장된 값이 stale 일 수 있음)
        int dayOfWeekNum = selected.getCandidateDate().getDayOfWeek().getValue();
        boolean currentlyAvailable = tutorAvailableTimeService.isWithinAvailableTime(
                contract.getTutorProfileNo(),
                dayOfWeekNum,
                selected.getCandidateStartTime(),
                selected.getCandidateStartTime().plusHours(1)
        );
        // 저장값과 어긋나면 동기화
        if (currentlyAvailable != selected.isAvailable()) {
            selected.setIsAvailable(currentlyAvailable);
        }

        if (!currentlyAvailable) {
            throw new ContractException("선택한 시간은 튜터님의 가능 시간대를 벗어납니다");
        }

        // 6. Contract 확정
        contract.confirmTrialTime(dto.getSelectedDate(), dto.getSelectedStartTime());

        // 7. 레슨 예약 생성 (튜터 일정에 노출되도록)
        lessonReserveService.reserveLessonsBatch(
                contract,
                List.of(LocalDateTime.of(dto.getSelectedDate(), dto.getSelectedStartTime()))
        );

        // 8. 선택되지 않은 후보들 삭제
        List<TrialContractCandidate> toDelete = candidates.stream()
                .filter(c -> !c.getId().equals(selected.getId()))
                .collect(Collectors.toList());
        trialCandidateRepository.deleteAll(toDelete);

        log.info("체험 레슨 확정 완료 - contractNo: {}", contractNo);

        return ContractResponseDto.fromEntity(contract);
    }

    /**
     * 체험 레슨 거절 (+ 대안 시간 제안)
     */
    @Transactional
    public ContractResponseDto rejectTrialContract(
            Long contractNo,
            Long tutorProfileNo,
            TrialRejectDto dto
    ) {
        log.info("체험 레슨 거절 - contractNo: {}, hasAlternatives: {}",
                contractNo, dto.hasAlternatives());

        // 1. Contract 조회 및 권한 체크
        StudentTutorContract contract = contractQueryService.getContract(contractNo);

        if (!contract.getTutorProfileNo().equals(tutorProfileNo)) {
            throw new ContractException("권한이 없습니다");
        }

        if (!contract.isTrial()) {
            throw new ContractException("체험 레슨이 아닙니다");
        }

        // 2. 대안 시간 제안이 있는 경우
        if (dto.hasAlternatives()) {
            return rejectWithAlternatives(contract, dto);
        }

        // 3. 단순 거절
        return rejectWithoutAlternatives(contract, dto.getReason());
    }

    /**
     * 학생이 튜터 제안 시간 수락
     */
    @Transactional
    public ContractResponseDto acceptTutorProposal(
            Long contractNo,
            Long studentNo,
            Long proposalId
    ) {
        log.info("튜터 제안 시간 수락 - contractNo: {}, proposalId: {}",
                contractNo, proposalId);

        // 1. Contract 조회 및 권한 체크
        StudentTutorContract contract = contractQueryService.getContract(contractNo);

        if (!contract.getStudentNo().equals(studentNo)) {
            throw new ContractException("권한이 없습니다");
        }

        // 2. Proposal 조회
        TrialContractProposal proposal = trialProposalRepository.findById(proposalId)
                .orElseThrow(() -> new ContractException("제안 시간을 찾을 수 없습니다"));

        if (!proposal.getContract().getContractNo().equals(contractNo)) {
            throw new ContractException("유효하지 않은 제안입니다");
        }

        // 3. Contract 확정
        contract.confirmTrialTime(
                proposal.getProposedDate(),
                proposal.getProposedStartTime()
        );

        // 3-1. 레슨 예약 생성 (튜터 일정에 노출되도록)
        lessonReserveService.reserveLessonsBatch(
                contract,
                List.of(LocalDateTime.of(
                        proposal.getProposedDate(),
                        proposal.getProposedStartTime()
                ))
        );

        // 4. Proposal 수락 처리
        proposal.setIsAccepted(true);
        trialProposalRepository.save(proposal);

        // 5. 나머지 제안들 거절 처리
        List<TrialContractProposal> otherProposals = trialProposalRepository
                .findByContract_ContractNoAndIsAcceptedIsNull(contractNo);

        otherProposals.stream()
                .filter(p -> !p.getId().equals(proposalId))
                .forEach(p -> p.setIsAccepted(false));

        trialProposalRepository.saveAll(otherProposals);

        log.info("튜터 제안 시간 수락 완료 - contractNo: {}", contractNo);

        return withTutorUserNo(ContractResponseDto.fromEntity(contract));
    }

    // ========== Private 헬퍼 메서드들 ==========

    private void validateTrialCandidates(
            List<ContractCreateRequestDto.TrialCandidateTime> candidates
    ) {
        if (candidates == null || candidates.isEmpty()) {
            throw new ContractException("최소 1개의 후보 시간을 선택해주세요");
        }

        if (candidates.size() > 3) {
            throw new ContractException("최대 3개까지 선택 가능합니다");
        }

        // 우선순위 중복 체크
        Set<Integer> priorities = candidates.stream()
                .map(ContractCreateRequestDto.TrialCandidateTime::getPriority)
                .collect(Collectors.toSet());

        if (priorities.size() != candidates.size()) {
            throw new ContractException("우선순위가 중복되었습니다");
        }

        // 과거 시간 체크
        LocalDateTime now = LocalDateTime.now();
        boolean hasPastTime = candidates.stream()
                .anyMatch(c -> {
                    LocalDateTime dt = LocalDateTime.of(
                            c.getCandidateDate(),
                            c.getCandidateStartTime()
                    );
                    return dt.isBefore(now);
                });

        if (hasPastTime) {
            throw new ContractException("과거 시간은 선택할 수 없습니다");
        }
    }

    private List<TrialContractCandidate> saveTrialCandidates(
            StudentTutorContract contract,
            List<ContractCreateRequestDto.TrialCandidateTime> candidateTimes
    ) {
        List<TrialContractCandidate> candidates = candidateTimes.stream()
                .map(c -> TrialContractCandidate.of(
                        contract,
                        c.getPriority(),
                        c.getCandidateDate(),
                        c.getCandidateStartTime()
                ))
                .collect(Collectors.toList());

        return trialCandidateRepository.saveAll(candidates);
    }

    private void checkAndUpdateCandidateAvailability(
            Long tutorProfileNo,
            List<TrialContractCandidate> candidates
    ) {
        for (TrialContractCandidate candidate : candidates) {
            LocalDateTime candidateDateTime = LocalDateTime.of(
                    candidate.getCandidateDate(),
                    candidate.getCandidateStartTime()
            );
            int dayOfWeekNum = candidateDateTime.getDayOfWeek().getValue();

            boolean isAvailable = tutorAvailableTimeService.isWithinAvailableTime(
                    tutorProfileNo,
                    dayOfWeekNum,
                    candidate.getCandidateStartTime(),
                    candidate.getCandidateStartTime().plusHours(1) // 1시간 기본
            );
            candidate.setIsAvailable(isAvailable);
        }
        trialCandidateRepository.saveAll(candidates);
    }

    private ContractResponseDto rejectWithAlternatives(
            StudentTutorContract contract,
            TrialRejectDto dto
    ) {
        log.info("대안 시간 제안과 함께 거절 - contractNo: {}, alternatives: {}",
                contract.getContractNo(), dto.getAlternativeTimes().size());

        // 1. 대안 시간 검증
        validateAlternativeTimes(dto.getAlternativeTimes());

        // 2. Contract memo에 거절 사유 추가 (상태는 REQUESTED 유지)
        String updatedMemo = (contract.getMemo() != null ? contract.getMemo() + "\n" : "") +
                "[튜터 응답] " + dto.getReason();
        contract.setMemo(updatedMemo);

        // 3. 학생이 처음 제안한 후보 시간 정리(튜터가 새 대안을 제시했으므로 더 이상 의미 없음)
        List<TrialContractCandidate> existingCandidates = trialCandidateRepository
                .findByContract_ContractNoOrderByPriority(contract.getContractNo());
        if (!existingCandidates.isEmpty()) {
            trialCandidateRepository.deleteAll(existingCandidates);
        }

        // 4. 기존에 미수락 상태로 남아있는 제안이 있다면 정리(중복 누적 방지)
        List<TrialContractProposal> staleProposals = trialProposalRepository
                .findByContract_ContractNoAndIsAcceptedIsNull(contract.getContractNo());
        if (!staleProposals.isEmpty()) {
            trialProposalRepository.deleteAll(staleProposals);
        }

        // 5. 대안 시간들 저장
        List<TrialContractProposal> proposals = dto.getAlternativeTimes().stream()
                .map(alt -> TrialContractProposal.of(
                        contract,
                        alt.getProposedDate(),
                        alt.getProposedStartTime()
                ))
                .collect(Collectors.toList());

        trialProposalRepository.saveAll(proposals);

        log.info("대안 시간 제안 완료 - contractNo: {}, proposals: {}",
                contract.getContractNo(), proposals.size());

        return ContractResponseDto.fromEntity(contract);
    }

    private ContractResponseDto rejectWithoutAlternatives(
            StudentTutorContract contract,
            String reason
    ) {
        log.info("체험 레슨 단순 거절 - contractNo: {}", contract.getContractNo());

        // 1. Contract 상태 변경
        contract.updateContractStatus(ContractStatus.CANCELLED);

        // 2. Memo에 거절 사유 추가
        String updatedMemo = (contract.getMemo() != null ? contract.getMemo() + "\n" : "") +
                "[튜터 거절] " + reason;
        contract.setMemo(updatedMemo);

        log.info("체험 레슨 거절 완료 - contractNo: {}", contract.getContractNo());

        return ContractResponseDto.fromEntity(contract);
    }

    private void validateAlternativeTimes(List<TrialRejectDto.AlternativeTime> times) {
        if (times == null || times.isEmpty()) {
            throw new ContractException("대안 시간을 입력해주세요");
        }

        if (times.size() > 5) {
            throw new ContractException("대안 시간은 최대 5개까지 제안 가능합니다");
        }

        // 과거 시간 체크
        LocalDateTime now = LocalDateTime.now();
        boolean hasPastTime = times.stream()
                .anyMatch(t -> {
                    LocalDateTime dt = LocalDateTime.of(
                            t.getProposedDate(),
                            t.getProposedStartTime()
                    );
                    return dt.isBefore(now);
                });

        if (hasPastTime) {
            throw new ContractException("과거 시간은 제안할 수 없습니다");
        }

        // 중복 체크
        Set<String> uniqueTimes = times.stream()
                .map(t -> t.getProposedDate() + "T" + t.getProposedStartTime())
                .collect(Collectors.toSet());

        if (uniqueTimes.size() != times.size()) {
            throw new ContractException("중복된 시간이 있습니다");
        }
    }

}
