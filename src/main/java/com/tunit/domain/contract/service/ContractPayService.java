package com.tunit.domain.contract.service;

import com.tunit.domain.contract.define.PaymentStatus;
import com.tunit.domain.contract.entity.StudentTutorContract;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContractPayService {

    private final ContractQueryService contractQueryService;

    @Transactional
    public void updatePaymentStatus(Long contractNo, PaymentStatus afterStatus) {
        var contract = contractQueryService.getContract(contractNo);

        verify(contract, afterStatus);

        switch (afterStatus) {
            case PAID -> contract.approvePayment(contract.getPaidAmount());
            case CONFIRMING -> {
                contract.updatePaymentStatus(afterStatus);
            }
        }

    }

    private void verify(StudentTutorContract contract, PaymentStatus afterStatus) {
        PaymentStatus beforeStatus = contract.getPaymentStatus();

        // 결제 상태 변경 검증 로직 추가
        beforeStatus.changeableByContractStatus(contract.getContractStatus());
        beforeStatus.changeTo(afterStatus);
    }

}
