package com.tunit.domain.notification.event;

import lombok.Getter;

/**
 * 수업 확정 이벤트
 */
@Getter
public class LessonConfirmedEvent extends NotificationEvent {
    private final String lessonTitle;
    private final String lessonDate;
    private final Long lessonNo;

    public LessonConfirmedEvent(Object source, Long userNo, Long lessonNo, String lessonTitle, String lessonDate) {
        super(source, userNo);
        this.lessonNo = lessonNo;
        this.lessonTitle = lessonTitle;
        this.lessonDate = lessonDate;
    }
}

