package com.tunit.domain.contract.service;

import com.tunit.domain.contract.define.ContractStatus;
import com.tunit.domain.contract.define.ContractType;
import com.tunit.domain.contract.dto.ContractCreateRequestDto;
import com.tunit.domain.contract.dto.TrialConfirmDto;
import com.tunit.domain.contract.dto.TrialRejectDto;
import com.tunit.domain.contract.entity.StudentTutorContract;
import com.tunit.domain.contract.entity.TrialContractCandidate;
import com.tunit.domain.contract.exception.ContractException;
import com.tunit.domain.contract.repository.TrialContractCandidateRepository;
import com.tunit.domain.contract.repository.TrialContractProposalRepository;
import com.tunit.domain.lesson.define.LessonSubCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("체험 레슨 계약 검증 테스트")
class TrialContractServiceTest {

    @InjectMocks
    private ContractService contractService;

    @Mock
    private ContractQueryService contractQueryService;

    @Mock
    private TrialContractCandidateRepository trialCandidateRepository;

    @Mock
    private TrialContractProposalRepository trialProposalRepository;

    @Test
    @DisplayName("체험 레슨 생성 실패 - 후보 시간 없음")
    void createTrialContract_noCandidates_throwsException() {
        // given
        ContractCreateRequestDto dto = ContractCreateRequestDto.builder()
            .tutorProfileNo(1L)
            .studentNo(1L)
            .contractType(ContractType.TRIAL)
            .lessonCategory(LessonSubCategory.ENGLISH)
            .trialCandidates(new ArrayList<>())
            .build();

        // when & then
        assertThatThrownBy(() -> contractService.createTrialContract(dto))
            .isInstanceOf(ContractException.class)
            .hasMessageContaining("최소 1개의 후보 시간을 선택해주세요");
    }

    @Test
    @DisplayName("체험 레슨 생성 실패 - 후보 시간 4개 초과")
    void createTrialContract_tooManyCandidates_throwsException() {
        // given
        List<ContractCreateRequestDto.TrialCandidateTime> candidates = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            candidates.add(ContractCreateRequestDto.TrialCandidateTime.builder()
                .priority(i)
                .candidateDate(LocalDate.now().plusDays(i))
                .candidateStartTime(LocalTime.of(14, 0))
                .build());
        }

        ContractCreateRequestDto dto = ContractCreateRequestDto.builder()
            .tutorProfileNo(1L)
            .studentNo(1L)
            .contractType(ContractType.TRIAL)
            .lessonCategory(LessonSubCategory.ENGLISH)
            .trialCandidates(candidates)
            .build();

        // when & then
        assertThatThrownBy(() -> contractService.createTrialContract(dto))
            .isInstanceOf(ContractException.class)
            .hasMessageContaining("최대 3개까지 선택 가능합니다");
    }

    @Test
    @DisplayName("체험 레슨 생성 실패 - 과거 시간 선택")
    void createTrialContract_pastTime_throwsException() {
        // given
        List<ContractCreateRequestDto.TrialCandidateTime> candidates = new ArrayList<>();
        candidates.add(ContractCreateRequestDto.TrialCandidateTime.builder()
            .priority(1)
            .candidateDate(LocalDate.now().minusDays(1))
            .candidateStartTime(LocalTime.of(14, 0))
            .build());

        ContractCreateRequestDto dto = ContractCreateRequestDto.builder()
            .tutorProfileNo(1L)
            .studentNo(1L)
            .contractType(ContractType.TRIAL)
            .lessonCategory(LessonSubCategory.ENGLISH)
            .trialCandidates(candidates)
            .build();

        // when & then
        assertThatThrownBy(() -> contractService.createTrialContract(dto))
            .isInstanceOf(ContractException.class)
            .hasMessageContaining("과거 시간은 선택할 수 없습니다");
    }

    @Test
    @DisplayName("우선순위 중복 검증")
    void validateTrialCandidates_duplicatePriority_throwsException() {
        // given
        List<ContractCreateRequestDto.TrialCandidateTime> candidates = new ArrayList<>();
        candidates.add(ContractCreateRequestDto.TrialCandidateTime.builder()
            .priority(1)
            .candidateDate(LocalDate.now().plusDays(3))
            .candidateStartTime(LocalTime.of(14, 0))
            .build());
        candidates.add(ContractCreateRequestDto.TrialCandidateTime.builder()
            .priority(1)
            .candidateDate(LocalDate.now().plusDays(4))
            .candidateStartTime(LocalTime.of(15, 0))
            .build());

        ContractCreateRequestDto dto = ContractCreateRequestDto.builder()
            .tutorProfileNo(1L)
            .studentNo(1L)
            .contractType(ContractType.TRIAL)
            .lessonCategory(LessonSubCategory.ENGLISH)
            .trialCandidates(candidates)
            .build();

        // when & then
        assertThatThrownBy(() -> contractService.createTrialContract(dto))
            .isInstanceOf(ContractException.class)
            .hasMessageContaining("우선순위가 중복되었습니다");
    }

    @Test
    @DisplayName("체험 레슨 확정 실패 - 권한 없음")
    void confirmTrialContract_unauthorized_throwsException() {
        // given
        StudentTutorContract mockContract = mock(StudentTutorContract.class);
        when(mockContract.getTutorProfileNo()).thenReturn(1L);

        TrialConfirmDto confirmDto = TrialConfirmDto.builder()
            .selectedDate(LocalDate.now().plusDays(3))
            .selectedStartTime(LocalTime.of(14, 0))
            .build();

        when(contractQueryService.getContract(1L)).thenReturn(mockContract);

        // when & then
        assertThatThrownBy(() -> contractService.confirmTrialContract(1L, 999L, confirmDto))
            .isInstanceOf(ContractException.class)
            .hasMessageContaining("권한이 없습니다");
    }

    @Test
    @DisplayName("체험 레슨 확정 실패 - 체험 레슨 아님")
    void confirmTrialContract_notTrial_throwsException() {
        // given
        StudentTutorContract mockContract = mock(StudentTutorContract.class);
        when(mockContract.getTutorProfileNo()).thenReturn(1L);
        when(mockContract.isTrial()).thenReturn(false);

        TrialConfirmDto confirmDto = TrialConfirmDto.builder()
            .selectedDate(LocalDate.now().plusDays(3))
            .selectedStartTime(LocalTime.of(14, 0))
            .build();

        when(contractQueryService.getContract(1L)).thenReturn(mockContract);

        // when & then
        assertThatThrownBy(() -> contractService.confirmTrialContract(1L, 1L, confirmDto))
            .isInstanceOf(ContractException.class)
            .hasMessageContaining("체험 레슨이 아닙니다");
    }

    @Test
    @DisplayName("체험 레슨 확정 실패 - 이미 처리된 계약")
    void confirmTrialContract_alreadyProcessed_throwsException() {
        // given
        StudentTutorContract mockContract = mock(StudentTutorContract.class);
        when(mockContract.getTutorProfileNo()).thenReturn(1L);
        when(mockContract.isTrial()).thenReturn(true);
        when(mockContract.getContractStatus()).thenReturn(ContractStatus.ACTIVE);

        TrialConfirmDto confirmDto = TrialConfirmDto.builder()
            .selectedDate(LocalDate.now().plusDays(3))
            .selectedStartTime(LocalTime.of(14, 0))
            .build();

        when(contractQueryService.getContract(1L)).thenReturn(mockContract);

        // when & then
        assertThatThrownBy(() -> contractService.confirmTrialContract(1L, 1L, confirmDto))
            .isInstanceOf(ContractException.class)
            .hasMessageContaining("이미 처리된 계약입니다");
    }

    @Test
    @DisplayName("체험 레슨 확정 실패 - 후보 시간 없음")
    void confirmTrialContract_noCandidates_throwsException() {
        // given
        StudentTutorContract mockContract = mock(StudentTutorContract.class);
        when(mockContract.getTutorProfileNo()).thenReturn(1L);
        when(mockContract.isTrial()).thenReturn(true);
        when(mockContract.getContractStatus()).thenReturn(ContractStatus.REQUESTED);

        TrialConfirmDto confirmDto = TrialConfirmDto.builder()
            .selectedDate(LocalDate.now().plusDays(3))
            .selectedStartTime(LocalTime.of(14, 0))
            .build();

        when(contractQueryService.getContract(1L)).thenReturn(mockContract);
        when(trialCandidateRepository.findByContract_ContractNoOrderByPriority(1L))
            .thenReturn(new ArrayList<>());

        // when & then
        assertThatThrownBy(() -> contractService.confirmTrialContract(1L, 1L, confirmDto))
            .isInstanceOf(ContractException.class)
            .hasMessageContaining("후보 시간이 없습니다");
    }

    @Test
    @DisplayName("대안 시간 검증 실패 - 과거 시간 제안")
    void rejectTrialContract_pastAlternativeTime_throwsException() {
        // given
        StudentTutorContract mockContract = mock(StudentTutorContract.class);
        when(mockContract.getTutorProfileNo()).thenReturn(1L);
        when(mockContract.isTrial()).thenReturn(true);

        List<TrialRejectDto.AlternativeTime> alternatives = new ArrayList<>();
        alternatives.add(TrialRejectDto.AlternativeTime.builder()
            .proposedDate(LocalDate.now().minusDays(1))
            .proposedStartTime(LocalTime.of(10, 0))
            .build());

        TrialRejectDto rejectDto = TrialRejectDto.builder()
            .reason("과거 시간 제안")
            .alternativeTimes(alternatives)
            .build();

        when(contractQueryService.getContract(1L)).thenReturn(mockContract);

        // when & then
        assertThatThrownBy(() -> contractService.rejectTrialContract(1L, 1L, rejectDto))
            .isInstanceOf(ContractException.class)
            .hasMessageContaining("과거 시간은 제안할 수 없습니다");
    }

    @Test
    @DisplayName("대안 시간 검증 실패 - 5개 초과")
    void rejectTrialContract_tooManyAlternatives_throwsException() {
        // given
        StudentTutorContract mockContract = mock(StudentTutorContract.class);
        when(mockContract.getTutorProfileNo()).thenReturn(1L);
        when(mockContract.isTrial()).thenReturn(true);

        List<TrialRejectDto.AlternativeTime> alternatives = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            alternatives.add(TrialRejectDto.AlternativeTime.builder()
                .proposedDate(LocalDate.now().plusDays(i))
                .proposedStartTime(LocalTime.of(10, 0))
                .build());
        }

        TrialRejectDto rejectDto = TrialRejectDto.builder()
            .reason("대안 시간 6개 제안")
            .alternativeTimes(alternatives)
            .build();

        when(contractQueryService.getContract(1L)).thenReturn(mockContract);

        // when & then
        assertThatThrownBy(() -> contractService.rejectTrialContract(1L, 1L, rejectDto))
            .isInstanceOf(ContractException.class)
            .hasMessageContaining("최대 5개까지 제안 가능합니다");
    }

    @Test
    @DisplayName("대안 시간 검증 실패 - 중복된 시간")
    void rejectTrialContract_duplicateAlternatives_throwsException() {
        // given
        StudentTutorContract mockContract = mock(StudentTutorContract.class);
        when(mockContract.getTutorProfileNo()).thenReturn(1L);
        when(mockContract.isTrial()).thenReturn(true);

        List<TrialRejectDto.AlternativeTime> alternatives = new ArrayList<>();
        alternatives.add(TrialRejectDto.AlternativeTime.builder()
            .proposedDate(LocalDate.now().plusDays(7))
            .proposedStartTime(LocalTime.of(10, 0))
            .build());
        alternatives.add(TrialRejectDto.AlternativeTime.builder()
            .proposedDate(LocalDate.now().plusDays(7))
            .proposedStartTime(LocalTime.of(10, 0))
            .build());

        TrialRejectDto rejectDto = TrialRejectDto.builder()
            .reason("중복 시간")
            .alternativeTimes(alternatives)
            .build();

        when(contractQueryService.getContract(1L)).thenReturn(mockContract);

        // when & then
        assertThatThrownBy(() -> contractService.rejectTrialContract(1L, 1L, rejectDto))
            .isInstanceOf(ContractException.class)
            .hasMessageContaining("중복된 시간이 있습니다");
    }

    @Test
    @DisplayName("학생이 튜터 제안 수락 실패 - 권한 없음")
    void acceptTutorProposal_unauthorized_throwsException() {
        // given
        StudentTutorContract mockContract = mock(StudentTutorContract.class);
        when(mockContract.getStudentNo()).thenReturn(1L);

        when(contractQueryService.getContract(1L)).thenReturn(mockContract);

        // when & then
        assertThatThrownBy(() -> contractService.acceptTutorProposal(1L, 999L, 1L))
            .isInstanceOf(ContractException.class)
            .hasMessageContaining("권한이 없습니다");
    }

    @Test
    @DisplayName("학생이 튜터 제안 수락 실패 - 제안 없음")
    void acceptTutorProposal_proposalNotFound_throwsException() {
        // given
        StudentTutorContract mockContract = mock(StudentTutorContract.class);
        when(mockContract.getStudentNo()).thenReturn(1L);

        when(contractQueryService.getContract(1L)).thenReturn(mockContract);
        when(trialProposalRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> contractService.acceptTutorProposal(1L, 1L, 999L))
            .isInstanceOf(ContractException.class)
            .hasMessageContaining("제안 시간을 찾을 수 없습니다");
    }
}
