package com.tunit.domain.notification.event;

import lombok.Getter;

/**
 * 수업 취소 이벤트
 */
@Getter
public class LessonCancelledEvent extends NotificationEvent {
    private final String lessonTitle;
    private final String reason;

    public LessonCancelledEvent(Object source, Long userNo, String lessonTitle, String reason) {
        super(source, userNo);
        this.lessonTitle = lessonTitle;
        this.reason = reason;
    }
}

