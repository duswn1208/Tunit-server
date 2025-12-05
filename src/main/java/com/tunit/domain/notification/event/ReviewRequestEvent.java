package com.tunit.domain.notification.event;

import lombok.Getter;

/**
 * 리뷰 요청 이벤트
 */
@Getter
public class ReviewRequestEvent extends NotificationEvent {
    private final String lessonTitle;

    public ReviewRequestEvent(Object source, Long userNo, String lessonTitle) {
        super(source, userNo);
        this.lessonTitle = lessonTitle;
    }
}

