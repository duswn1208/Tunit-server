package com.tunit.domain.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentRejectRequestDto {
    private String rejectReason;
}

