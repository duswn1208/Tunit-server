package com.tunit.domain.payment.dto;

import com.tunit.domain.payment.define.PaymentMethod;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentRequestCreateDto {
    private Long contractNo;
    private Integer paymentAmount;
    private PaymentMethod paymentMethod;
    private String proofUrl; // 입금증 URL
}
