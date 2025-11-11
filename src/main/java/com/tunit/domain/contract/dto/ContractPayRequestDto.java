package com.tunit.domain.contract.dto;

import com.tunit.domain.contract.define.ContractStatus;
import com.tunit.domain.contract.define.PaymentStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ContractPayRequestDto {
    private Long contractNo;
    private ContractStatus contractStatus;
    private Long paidAmount;
    private PaymentStatus paymentStatus;
    private String cancelReason;
}
