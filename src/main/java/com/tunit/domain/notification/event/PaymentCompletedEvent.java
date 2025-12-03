package com.tunit.domain.notification.event;

import lombok.Getter;

/**
 * 결제 완료 이벤트
 */
@Getter
public class PaymentCompletedEvent extends NotificationEvent {
    private final String itemName;
    private final int amount;

    public PaymentCompletedEvent(Object source, Long userNo, String itemName, int amount) {
        super(source, userNo);
        this.itemName = itemName;
        this.amount = amount;
    }
}

