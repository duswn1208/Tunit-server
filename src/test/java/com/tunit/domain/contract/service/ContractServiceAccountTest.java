package com.tunit.domain.contract.service;

import com.tunit.domain.contract.define.ContractStatus;
import com.tunit.domain.contract.define.PaymentStatus;
import com.tunit.domain.contract.dto.ContractResponseDto;
import com.tunit.domain.contract.entity.StudentTutorContract;
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
import com.tunit.domain.user.entity.UserMain;
import com.tunit.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ContractService 계좌 관련 테스트")
class ContractServiceAccountTest {

    @InjectMocks
    private ContractService contractService;

    @Mock
    private ContractQueryService contractQueryService;

    @Mock
    private LessonReserveProcessorService lessonReserveService;

    @Mock
    private LessonManagementService lessonManagementService;

    @Mock
    private LessonQueryService lessonQueryService;

    @Mock
    private TutorProfileService tutorProfileService;

    @Mock
    private UserService userService;

    @Mock
    private NotificationEventService notificationEventService;

    @Mock
    private TrialContractCandidateRepository trialCandidateRepository;

    @Mock
    private TrialContractProposalRepository trialProposalRepository;

    @Mock
    private TutorAvailableTimeService tutorAvailableTimeService;

    // ========== updateContractStatus - APPROVED ==========

    @Nested
    @DisplayName("updateContractStatus() APPROVED 처리")
    class UpdateContractStatusApproved {

        @Test
        @DisplayName("튜터 계좌가 등록되어 있으면 입금 안내 알림을 전송한다")
        void approved_withAccountInfo_sendsNotification() {
            // given
            StudentTutorContract contract = StudentTutorContract.builder()
                    .tutorProfileNo(1L)
                    .studentNo(10L)
                    .contractStatus(ContractStatus.REQUESTED)
                    .totalPrice(120000)
                    .build();

            TutorProfile tutorProfile = TutorProfile.of()
                    .tutorProfileNo(1L)
                    .userNo(99L)
                    .bankName("국민은행")
                    .accountNumber("123-456-789012")
                    .accountHolder("홍길동")
                    .build();

            UserMain tutor = UserMain.of().userNo(99L).name("홍길동").build();

            given(contractQueryService.getContract(42L)).willReturn(contract);
            given(tutorProfileService.findByTutorProfileNo(1L)).willReturn(tutorProfile);
            given(userService.findByUserNo(99L)).willReturn(tutor);

            // when
            contractService.updateContractStatus(42L, 1L, ContractStatus.APPROVED, null, true);

            // then
            ArgumentCaptor<Long> studentNoCaptor = ArgumentCaptor.forClass(Long.class);
            ArgumentCaptor<String> bankNameCaptor = ArgumentCaptor.forClass(String.class);
            verify(notificationEventService).sendPaymentAccountNotification(
                    studentNoCaptor.capture(), any(), eq("홍길동"), eq(120000),
                    bankNameCaptor.capture(), eq("123-456-789012"), eq("홍길동")
            );
            assertThat(studentNoCaptor.getValue()).isEqualTo(10L);
            assertThat(bankNameCaptor.getValue()).isEqualTo("국민은행");
        }

        @Test
        @DisplayName("튜터 계좌가 미등록이면 알림을 보내지 않고 정상 진행한다")
        void approved_withoutAccountInfo_skipsNotification() {
            // given
            StudentTutorContract contract = StudentTutorContract.builder()
                    .tutorProfileNo(1L)
                    .studentNo(10L)
                    .contractStatus(ContractStatus.REQUESTED)
                    .totalPrice(120000)
                    .build();

            TutorProfile tutorProfile = TutorProfile.of()
                    .tutorProfileNo(1L)
                    .userNo(99L)
                    .build(); // bankName, accountNumber null

            given(contractQueryService.getContract(42L)).willReturn(contract);
            given(tutorProfileService.findByTutorProfileNo(1L)).willReturn(tutorProfile);

            // when
            contractService.updateContractStatus(42L, 1L, ContractStatus.APPROVED, null, true);

            // then
            verify(notificationEventService, never()).sendPaymentAccountNotification(
                    any(), any(), any(), anyInt(), any(), any(), any()
            );
        }

        @Test
        @DisplayName("APPROVED 처리 시 PaymentStatus가 PENDING으로 변경된다")
        void approved_updatesPaymentStatusToPending() {
            // given
            StudentTutorContract contract = StudentTutorContract.builder()
                    .tutorProfileNo(1L)
                    .studentNo(10L)
                    .contractStatus(ContractStatus.REQUESTED)
                    .totalPrice(120000)
                    .build();

            TutorProfile tutorProfile = TutorProfile.of()
                    .tutorProfileNo(1L)
                    .userNo(99L)
                    .build();

            given(contractQueryService.getContract(42L)).willReturn(contract);
            given(tutorProfileService.findByTutorProfileNo(1L)).willReturn(tutorProfile);

            // when
            contractService.updateContractStatus(42L, 1L, ContractStatus.APPROVED, null, true);

            // then
            assertThat(contract.getPaymentStatus()).isEqualTo(PaymentStatus.PENDING);
            assertThat(contract.getContractStatus()).isEqualTo(ContractStatus.APPROVED);
        }
    }

    // ========== getContract() 계좌 노출 조건 ==========

    @Nested
    @DisplayName("getContract() 계좌 정보 노출")
    class GetContractAccountInfo {

        @Test
        @DisplayName("APPROVED + PENDING 상태이면 계좌 정보가 포함된다")
        void getContract_approvedAndPending_includesAccountInfo() {
            // given
            StudentTutorContract contract = StudentTutorContract.builder()
                    .tutorProfileNo(1L)
                    .studentNo(10L)
                    .contractStatus(ContractStatus.APPROVED)
                    .paymentStatus(PaymentStatus.PENDING)
                    .totalPrice(120000)
                    .lessonCount(4)
                    .weekCount(4)
                    .build();

            TutorProfile tutorProfile = TutorProfile.of()
                    .tutorProfileNo(1L)
                    .bankName("국민은행")
                    .accountNumber("123-456-789012")
                    .accountHolder("홍길동")
                    .build();

            given(contractQueryService.getContract(42L)).willReturn(contract);
            given(lessonQueryService.findByContractNoAndStatusIn(eq(42L), any())).willReturn(List.of());
            given(tutorProfileService.findByTutorProfileNo(1L)).willReturn(tutorProfile);

            // when
            ContractResponseDto response = contractService.getContract(42L);

            // then
            assertThat(response.getTutorBankName()).isEqualTo("국민은행");
            assertThat(response.getTutorAccountNumber()).isEqualTo("123-456-789012");
            assertThat(response.getTutorAccountHolder()).isEqualTo("홍길동");
        }

        @Test
        @DisplayName("APPROVED + CONFIRMING 상태이면 계좌 정보가 포함된다")
        void getContract_approvedAndConfirming_includesAccountInfo() {
            // given
            StudentTutorContract contract = StudentTutorContract.builder()
                    .tutorProfileNo(1L)
                    .studentNo(10L)
                    .contractStatus(ContractStatus.APPROVED)
                    .paymentStatus(PaymentStatus.CONFIRMING)
                    .totalPrice(120000)
                    .lessonCount(4)
                    .weekCount(4)
                    .build();

            TutorProfile tutorProfile = TutorProfile.of()
                    .tutorProfileNo(1L)
                    .bankName("신한은행")
                    .accountNumber("110-111-222222")
                    .accountHolder("김철수")
                    .build();

            given(contractQueryService.getContract(42L)).willReturn(contract);
            given(lessonQueryService.findByContractNoAndStatusIn(eq(42L), any())).willReturn(List.of());
            given(tutorProfileService.findByTutorProfileNo(1L)).willReturn(tutorProfile);

            // when
            ContractResponseDto response = contractService.getContract(42L);

            // then
            assertThat(response.getTutorBankName()).isEqualTo("신한은행");
            assertThat(response.getTutorAccountNumber()).isEqualTo("110-111-222222");
            assertThat(response.getTutorAccountHolder()).isEqualTo("김철수");
        }

        @Test
        @DisplayName("결제 완료(PAID) 상태이면 계좌 정보가 null이다")
        void getContract_paid_accountInfoIsNull() {
            // given
            StudentTutorContract contract = StudentTutorContract.builder()
                    .tutorProfileNo(1L)
                    .studentNo(10L)
                    .contractStatus(ContractStatus.APPROVED)
                    .paymentStatus(PaymentStatus.PAID)
                    .totalPrice(120000)
                    .lessonCount(4)
                    .weekCount(4)
                    .build();

            given(contractQueryService.getContract(42L)).willReturn(contract);
            given(lessonQueryService.findByContractNoAndStatusIn(eq(42L), any())).willReturn(List.of());

            // when
            ContractResponseDto response = contractService.getContract(42L);

            // then
            assertThat(response.getTutorBankName()).isNull();
            assertThat(response.getTutorAccountNumber()).isNull();
            assertThat(response.getTutorAccountHolder()).isNull();
            verify(tutorProfileService, never()).findByTutorProfileNo(any());
        }

        @Test
        @DisplayName("계약 상태가 APPROVED가 아니면 계좌 정보가 null이다")
        void getContract_notApproved_accountInfoIsNull() {
            // given
            StudentTutorContract contract = StudentTutorContract.builder()
                    .tutorProfileNo(1L)
                    .studentNo(10L)
                    .contractStatus(ContractStatus.ACTIVE)
                    .paymentStatus(PaymentStatus.PAID)
                    .totalPrice(120000)
                    .lessonCount(4)
                    .weekCount(4)
                    .build();

            given(contractQueryService.getContract(42L)).willReturn(contract);
            given(lessonQueryService.findByContractNoAndStatusIn(eq(42L), any())).willReturn(List.of());

            // when
            ContractResponseDto response = contractService.getContract(42L);

            // then
            assertThat(response.getTutorBankName()).isNull();
            verify(tutorProfileService, never()).findByTutorProfileNo(any());
        }
    }
}
