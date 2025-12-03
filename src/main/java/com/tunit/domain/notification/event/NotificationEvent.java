package com.tunit.domain.notification.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 알림 이벤트 베이스 클래스
 */
@Getter
public abstract class NotificationEvent extends ApplicationEvent {

    private final Long userNo;

    protected NotificationEvent(Object source, Long userNo) {
        super(source);
        this.userNo = userNo;
    }
}

