package com.tunit.domain.notification.controller;

import com.tunit.common.session.annotation.LoginUser;
import com.tunit.domain.notification.define.NotificationType;
import com.tunit.domain.notification.dto.DeviceTokenRegisterDto;
import com.tunit.domain.notification.dto.NotificationResponseDto;
import com.tunit.domain.notification.dto.PushNotificationDto;
import com.tunit.domain.notification.entity.UserDeviceToken;
import com.tunit.domain.notification.service.DeviceTokenService;
import com.tunit.domain.notification.service.NotificationService;
import com.tunit.domain.notification.service.PushNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 푸시 알림 API 컨트롤러
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final DeviceTokenService deviceTokenService;
    private final PushNotificationService pushNotificationService;

    /**
     * 디바이스 토큰 등록
     */
    @PostMapping("/device-token")
    public ResponseEntity<?> registerDeviceToken(
            @LoginUser(field = "userNo") Long userNo,
            @RequestBody DeviceTokenRegisterDto dto) {

        UserDeviceToken token = deviceTokenService.registerOrUpdateToken(userNo, dto);

        return ResponseEntity.ok().body(token.getTokenNo());
    }

    /**
     * 디바이스 토큰 비활성화 (로그아웃)
     */
    @DeleteMapping("/device-token")
    public ResponseEntity<?> deactivateDeviceToken(
            @RequestParam String fcmToken) {

        deviceTokenService.deactivateToken(fcmToken);
        return ResponseEntity.ok().build();
    }

    /**
     * 사용자의 활성 디바이스 목록 조회
     */
    @GetMapping("/devices")
    public ResponseEntity<List<UserDeviceToken>> getActiveDevices(
            @LoginUser(field = "userNo") Long userNo) {

        List<UserDeviceToken> devices = deviceTokenService.getActiveDevices(userNo);

        return ResponseEntity.ok(devices);
    }

    /**
     * 사용자의 알림 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<NotificationResponseDto>> getNotifications(
            @LoginUser(field = "userNo") Long userNo) {

        List<NotificationResponseDto> notifications = notificationService.getUserNotifications(userNo);

        return ResponseEntity.ok(notifications);
    }

    /**
     * 사용자의 특정 타입 알림 조회
     */
    @GetMapping("/type/{notificationType}")
    public ResponseEntity<List<NotificationResponseDto>> getNotificationsByType(
            @LoginUser(field = "userNo") Long userNo,
            @PathVariable NotificationType notificationType) {

        List<NotificationResponseDto> notifications =
                notificationService.getUserNotificationsByType(userNo, notificationType);

        return ResponseEntity.ok(notifications);
    }

    /**
     * 최근 N일간의 알림 조회
     */
    @GetMapping("/recent/{days}")
    public ResponseEntity<List<NotificationResponseDto>> getRecentNotifications(
            @LoginUser(field = "userNo") Long userNo,
            @PathVariable int days) {

        List<NotificationResponseDto> notifications =
                notificationService.getRecentNotifications(userNo, days);

        return ResponseEntity.ok(notifications);
    }

    /**
     * 읽지 않은 알림 개수 조회
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(
            @LoginUser(field = "userNo") Long userNo) {

        long count = notificationService.getUnreadCount(userNo);

        return ResponseEntity.ok(count);
    }

    /**
     * 읽지 않은 알림 조회
     */
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponseDto>> getUnreadNotifications(
            @LoginUser(field = "userNo") Long userNo) {
        List<NotificationResponseDto> notifications =
                notificationService.getUnreadNotifications(userNo);
        return ResponseEntity.ok(notifications);
    }

    /**
     * 알림 읽음 처리
     */
    @PutMapping("/{notifyNo}/read")
    public ResponseEntity<?> markAsRead(
            @LoginUser(field = "userNo") Long userNo,
            @PathVariable Long notifyNo) {

        notificationService.markAsRead(notifyNo, userNo);

        return ResponseEntity.ok().build();
    }

    /**
     * 모든 알림 읽음 처리
     */
    @PutMapping("/read-all")
    public ResponseEntity<?> markAllAsRead(
            @LoginUser(field = "userNo") Long userNo) {

        notificationService.markAllAsRead(userNo);

        return ResponseEntity.ok().build();
    }

    /**
     * 알림 삭제
     */
    @DeleteMapping("/{notifyNo}")
    public ResponseEntity<?> deleteNotification(
            @LoginUser(field = "userNo") Long userNo,
            @PathVariable Long notifyNo) {

        notificationService.deleteNotification(notifyNo, userNo);

        return ResponseEntity.ok().build();
    }

    /**
     * 모든 알림 삭제
     */
    @DeleteMapping
    public ResponseEntity<?> deleteAllNotifications(
            @LoginUser(field = "userNo") Long userNo) {

        notificationService.deleteAllNotifications(userNo);

        return ResponseEntity.ok().build();
    }

    /**
     * 푸시 알림 전송 (관리자용)
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendNotification(@RequestBody PushNotificationDto dto) {
        pushNotificationService.sendPushNotification(dto);
        return ResponseEntity.ok().build();
    }

    /**
     * 일괄 푸시 알림 전송 (관리자용)
     */
    @PostMapping("/send-bulk")
    public ResponseEntity<?> sendBulkNotification(
            @RequestParam List<Long> userNos,
            @RequestBody PushNotificationDto dto) {

        pushNotificationService.sendBulkNotification(userNos, dto);
        return ResponseEntity.ok().build();
    }
}
