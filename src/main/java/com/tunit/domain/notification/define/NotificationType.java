package com.tunit.domain.notification.define;

/**
 * 알림 타입
 */
public enum NotificationType {
    LESSON_REMINDER,        // 수업 알림
    LESSON_CONFIRMED,       // 수업 확정
    LESSON_CANCELLED,       // 수업 취소
    REVIEW_REQUEST,         // 리뷰 요청
    CONTRACT_SIGNED,        // 계약 체결
    CONTRACT_EXPIRED,       // 계약 만료
    PAYMENT_COMPLETED,      // 결제 완료
    SYSTEM_NOTICE,          // 시스템 공지
    MARKETING              // 마케팅 알림
}

