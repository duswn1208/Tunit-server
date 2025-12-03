package com.tunit.domain.notification.service;

import com.google.firebase.messaging.*;
import com.tunit.domain.notification.define.NotificationStatus;
import com.tunit.domain.notification.dto.PushNotificationDto;
import com.tunit.domain.notification.entity.NotifyPush;
import com.tunit.domain.notification.entity.UserDeviceToken;
import com.tunit.domain.notification.repository.NotifyPushRepository;
import com.tunit.domain.notification.repository.UserDeviceTokenRepository;
import com.tunit.domain.user.entity.UserMain;
import com.tunit.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 푸시 알림 전송 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PushNotificationService {

    private final NotifyPushRepository notifyPushRepository;
    private final UserDeviceTokenRepository deviceTokenRepository;
    private final UserService userService;

    /**
     * 특정 사용자에게 푸시 알림 전송
     */
    public void sendPushNotification(PushNotificationDto dto) {
        UserMain user = userService.findByUserNo(dto.getUserNo());

        // 알림 이력 저장
        NotifyPush notifyPush = NotifyPush.builder()
                .user(user)
                .notificationType(dto.getNotificationType())
                .title(dto.getTitle())
                .message(dto.getMessage())
                .deepLink(dto.getDeepLink())
                .data(dto.getData())
                .status(NotificationStatus.PENDING)
                .build();

        notifyPush = notifyPushRepository.save(notifyPush);

        // 사용자의 활성화된 모든 디바이스로 전송
        List<UserDeviceToken> deviceTokens = deviceTokenRepository.findByUserAndIsActiveTrue(user);

        if (deviceTokens.isEmpty()) {
            log.warn("사용자 {}의 활성화된 디바이스 토큰이 없습니다..", user.getUserNo());
            return;
        }

        boolean sentSuccess = false;
        StringBuilder errorMessages = new StringBuilder();

        for (UserDeviceToken deviceToken : deviceTokens) {
            try {
                String messageId = sendToDevice(deviceToken.getFcmToken(), dto);
                sentSuccess = true;
                deviceToken.updateLastUsedAt();
                log.info("푸시 알림 전송 성공 - MessageId: {}, User: {}, Device: {}",
                        messageId, user.getUserNo(), deviceToken.getDeviceType());
            } catch (FirebaseMessagingException e) {
                log.error("푸시 알림 전송 실패 - User: {}, Device: {}, Error: {}",
                        user.getUserNo(), deviceToken.getDeviceType(), e.getMessage());
                errorMessages.append(deviceToken.getDeviceType()).append(": ").append(e.getMessage()).append("; ");

                // 토큰이 유효하지 않으면 비활성화
                if (isInvalidToken(e)) {
                    deviceToken.deactivate();
                }
            }
        }

        if (sentSuccess) {
            notifyPush.markAsSent(null);
        } else {
            notifyPush.markAsFailed(errorMessages.toString());
        }
    }

    /**
     * 단일 디바이스로 FCM 메시지 전송
     */
    private String sendToDevice(String fcmToken, PushNotificationDto dto) throws FirebaseMessagingException {
        Notification notification = Notification.builder()
                .setTitle(dto.getTitle())
                .setBody(dto.getMessage())
                .build();

        Map<String, String> data = new HashMap<>();
        data.put("notificationType", dto.getNotificationType().name());
        if (dto.getDeepLink() != null) {
            data.put("deepLink", dto.getDeepLink());
        }
        if (dto.getData() != null) {
            data.put("data", dto.getData());
        }

        Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(notification)
                .putAllData(data)
                .setAndroidConfig(AndroidConfig.builder()
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .setNotification(AndroidNotification.builder()
                                .setClickAction("FLUTTER_NOTIFICATION_CLICK")
                                .build())
                        .build())
                .setApnsConfig(ApnsConfig.builder()
                        .setAps(Aps.builder()
                                .setContentAvailable(true)
                                .build())
                        .build())
                .setWebpushConfig(WebpushConfig.builder()
                        .setNotification(new WebpushNotification(
                                dto.getTitle(),
                                dto.getMessage(),
                                dto.getImageUrl()))
                        .build())
                .build();

        return FirebaseMessaging.getInstance().send(message);
    }

    /**
     * 여러 사용자에게 일괄 전송
     */
    public void sendBulkNotification(List<Long> userNos, PushNotificationDto baseDto) {
        for (Long userNo : userNos) {
            try {
                PushNotificationDto dto = PushNotificationDto.builder()
                        .userNo(userNo)
                        .notificationType(baseDto.getNotificationType())
                        .title(baseDto.getTitle())
                        .message(baseDto.getMessage())
                        .imageUrl(baseDto.getImageUrl())
                        .deepLink(baseDto.getDeepLink())
                        .data(baseDto.getData())
                        .build();

                sendPushNotification(dto);
            } catch (Exception e) {
                log.error("일괄 전송 중 오류 발생 - User: {}, Error: {}", userNo, e.getMessage());
            }
        }
    }

    /**
     * 전송 실패한 알림 재전송
     */
    public void retryFailedNotifications() {
        List<NotifyPush> failedNotifications = notifyPushRepository
                .findByStatusOrderByCreatedAtAsc(NotificationStatus.FAILED);

        for (NotifyPush notification : failedNotifications) {
            try {
                PushNotificationDto dto = PushNotificationDto.builder()
                        .userNo(notification.getUser().getUserNo())
                        .notificationType(notification.getNotificationType())
                        .title(notification.getTitle())
                        .message(notification.getMessage())
                        .deepLink(notification.getDeepLink())
                        .data(notification.getData())
                        .build();

                notification.resetForRetry();
                sendPushNotification(dto);
            } catch (Exception e) {
                log.error("재전송 실패 - NotifyNo: {}, Error: {}",
                        notification.getNotifyNo(), e.getMessage());
            }
        }
    }

    /**
     * 토큰이 유효하지 않은 에러인지 확인
     */
    private boolean isInvalidToken(FirebaseMessagingException e) {
        String errorCode = String.valueOf(e.getErrorCode());
        return "registration-token-not-registered".equals(errorCode)
                || "invalid-registration-token".equals(errorCode)
                || "invalid-argument".equals(errorCode);
    }
}
