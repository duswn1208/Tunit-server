package com.tunit.domain.contract.dto;

import com.tunit.domain.contract.define.ContractStatus;
import com.tunit.domain.contract.define.PaymentStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.antlr.v4.runtime.misc.NotNull;

@Getter
@NoArgsConstructor
public class ContractStatusUpdateRequestDto {
    private ContractStatus contractStatus;
    private PaymentStatus paymentStatus;
    private Integer paidAmount;
    private String cancelReason;
}

