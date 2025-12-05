package com.tunit.domain.notification.define;

import java.util.List;

/**
 * 알림 전송 상태
 */
public enum NotificationStatus {
    PENDING,    // 전송 대기
    SENT,       // 전송 완료
    FAILED,     // 전송 실패
    READ;        // 읽음

    public static List<NotificationStatus> getReadableStatuses() {
        return List.of(SENT, PENDING);
    }
}

