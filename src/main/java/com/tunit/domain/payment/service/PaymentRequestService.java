package com.tunit.domain.payment.service;

import com.tunit.domain.contract.entity.StudentTutorContract;
import com.tunit.domain.contract.exception.ContractException;
import com.tunit.domain.contract.repository.StudentTutorContractRepository;
import com.tunit.domain.contract.service.ContractService;
import com.tunit.domain.payment.define.PaymentRequestStatus;
import com.tunit.domain.payment.dto.PaymentRequestCreateDto;
import com.tunit.domain.payment.dto.PaymentRequestResponseDto;
import com.tunit.domain.payment.entity.PaymentRequest;
import com.tunit.domain.payment.repository.PaymentRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PaymentRequestService {

    private final PaymentRequestRepository paymentRequestRepository;
    private final StudentTutorContractRepository contractRepository;
    private final ContractService contractService;

    /**
     * 결제 확인 요청 생성 (학생)
     */
    @Transactional
    public PaymentRequestResponseDto createPaymentRequest(Long studentNo, PaymentRequestCreateDto requestDto) {
        // 계약 조회
        StudentTutorContract contract = contractRepository.findById(requestDto.getContractNo())
                .orElseThrow(() -> new ContractException("계약을 찾을 수 없습니다."));

        // 본인 계약인지 확인
        if (!contract.getStudentNo().equals(studentNo)) {
            throw new ContractException("본인의 계약만 결제 요청할 수 있습니다.");
        }

        // 이미 확인 요청 중인지 확인
        paymentRequestRepository.findByContractNoAndStatus(requestDto.getContractNo(), PaymentRequestStatus.REQUESTED)
                .ifPresent(pr -> {
                    throw new IllegalStateException("이미 확인 요청 중인 결제가 있습니다.");
                });

        // PaymentRequest 생성
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .contractNo(requestDto.getContractNo())
                .studentNo(studentNo)
                .tutorProfileNo(contract.getTutorProfileNo())
                .paymentAmount(requestDto.getPaymentAmount())
                .paymentMethod(requestDto.getPaymentMethod())
                .proofUrl(requestDto.getProofUrl())
                .status(PaymentRequestStatus.REQUESTED)
                .build();

        PaymentRequest saved = paymentRequestRepository.save(paymentRequest);

        contractService.updateToConfirming(requestDto.getContractNo(), contract.getStudentNo());
        return new PaymentRequestResponseDto(saved);
    }

    /**
     * 결제 확인 (튜터)
     */
    @Transactional
    public PaymentRequestResponseDto confirmPayment(Long paymentNo, Long tutorProfileNo) {
        PaymentRequest paymentRequest = paymentRequestRepository.findById(paymentNo)
                .orElseThrow(() -> new ContractException("결제 요청을 찾을 수 없습니다."));

        // 본인의 결제 요청인지 확인
        if (!paymentRequest.getTutorProfileNo().equals(tutorProfileNo)) {
            throw new ContractException("본인의 결제 요청만 확인할 수 있���니다.");
        }

        // 결제 확인
        paymentRequest.confirm();
        contractService.updateToCompleted(paymentRequest.getContractNo(), paymentRequest.getTutorProfileNo(), paymentRequest.getPaymentAmount());

        return new PaymentRequestResponseDto(paymentRequest);
    }

    /**
     * 결제 거절 (튜터)
     */
    @Transactional
    public PaymentRequestResponseDto rejectPayment(Long paymentNo, Long tutorProfileNo, String rejectReason) {
        PaymentRequest paymentRequest = paymentRequestRepository.findById(paymentNo)
                .orElseThrow(() -> new ContractException("결제 요청을 찾을 수 없습니다."));

        // 본인의 결제 요청인지 확인
        if (!paymentRequest.getTutorProfileNo().equals(tutorProfileNo)) {
            throw new ContractException("본인의 결제 요청만 거절할 수 있습니다.");
        }

        // 결제 거절
        paymentRequest.reject(rejectReason);

        return new PaymentRequestResponseDto(paymentRequest);
    }

    /**
     * 계약별 결제 요청 목록 조회
     */
    public List<PaymentRequestResponseDto> getPaymentRequestsByContract(Long contractNo) {
        return paymentRequestRepository.findByContractNo(contractNo).stream()
                .map(PaymentRequestResponseDto::new)
                .toList();
    }

    /**
     * 학생의 결제 요청 목록 조회
     */
    public List<PaymentRequestResponseDto> getPaymentRequestsByStudent(Long studentNo) {
        return paymentRequestRepository.findByStudentNo(studentNo).stream()
                .map(PaymentRequestResponseDto::new)
                .toList();
    }

    /**
     * 튜터의 결제 요청 목록 조회
     */
    public List<PaymentRequestResponseDto> getPaymentRequestsByTutor(Long tutorProfileNo) {
        return paymentRequestRepository.findByTutorProfileNo(tutorProfileNo).stream()
                .map(PaymentRequestResponseDto::new)
                .toList();
    }

    /**
     * 결제 요청 단건 조회
     */
    public PaymentRequestResponseDto getPaymentRequest(Long paymentNo) {
        PaymentRequest paymentRequest = paymentRequestRepository.findById(paymentNo)
                .orElseThrow(() -> new ContractException("결제 요청을 찾을 수 없습니다."));
        return new PaymentRequestResponseDto(paymentRequest);
    }
}
