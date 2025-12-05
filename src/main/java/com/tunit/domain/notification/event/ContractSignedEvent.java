package com.tunit.domain.notification.event;

import lombok.Getter;

/**
 * 계약 체결 이벤트
 */
@Getter
public class ContractSignedEvent extends NotificationEvent {
    private final Long tutorNo;
    private final Long studentNo;
    private final String contractTitle;

    public ContractSignedEvent(Object source, Long tutorNo, Long studentNo, String contractTitle) {
        super(source, tutorNo);  // tutorNo를 기본 userNo로
        this.tutorNo = tutorNo;
        this.studentNo = studentNo;
        this.contractTitle = contractTitle;
    }
}

