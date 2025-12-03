package com.tunit.domain.notification.dto;

import com.tunit.domain.notification.define.NotificationStatus;
import com.tunit.domain.notification.define.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 알림 조회 응답 DTO
 */
@Getter
@Builder
public class NotificationResponseDto {
    private Long notifyNo;
    private NotificationType notificationType;
    private String title;
    private String message;
    private String deepLink;
    private NotificationStatus status;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
}

