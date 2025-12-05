package com.tunit.domain.notification.dto;

import com.tunit.domain.notification.define.NotificationType;
import lombok.Builder;
import lombok.Getter;

/**
 * 푸시 알림 전송 요청 DTO
 */
@Getter
@Builder
public class PushNotificationDto {
    private Long userNo;
    private NotificationType notificationType;
    private String title;
    private String message;
    private String imageUrl;
    private String deepLink;
    private String data;  // JSON 형식의 추가 데이터
}

