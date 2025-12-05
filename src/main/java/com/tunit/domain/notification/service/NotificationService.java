package com.tunit.domain.notification.service;

import com.tunit.domain.notification.define.NotificationStatus;
import com.tunit.domain.notification.define.NotificationType;
import com.tunit.domain.notification.dto.NotificationResponseDto;
import com.tunit.domain.notification.entity.NotifyPush;
import com.tunit.domain.notification.repository.NotifyPushRepository;
import com.tunit.domain.user.entity.UserMain;
import com.tunit.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 알림 조회 및 관리 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NotificationService {

    private final NotifyPushRepository notifyPushRepository;
    private final UserService userService;

    /**
     * 사용자의 알림 목록 조회
     */
    public List<NotificationResponseDto> getUserNotifications(Long userNo) {
        UserMain user = userService.findByUserNo(userNo);

        List<NotifyPush> notifications = notifyPushRepository.findByUserOrderByCreatedAtDesc(user);

        return notifications.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 사용자의 특정 타입 알림 조회
     */
    public List<NotificationResponseDto> getUserNotificationsByType(Long userNo, NotificationType type) {
        UserMain user = userService.findByUserNo(userNo);

        List<NotifyPush> notifications = notifyPushRepository
                .findByUserAndNotificationTypeOrderByCreatedAtDesc(user, type);

        return notifications.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 사용자의 읽지 않은 알림 개수 조회
     */
    public long getUnreadCount(Long userNo) {
        UserMain user = userService.findByUserNo(userNo);

        return notifyPushRepository.countByUserAndStatusIn(user, NotificationStatus.getReadableStatuses());
    }

    /**
     * 사용자의 읽지 않은 알림 조회
     * @param userNo
     * @return
     */
    public List<NotificationResponseDto> getUnreadNotifications(Long userNo) {
        UserMain user = userService.findByUserNo(userNo);

        List<NotifyPush> notifications = notifyPushRepository
                .findByUserAndStatusInOrderByCreatedAtDesc(user, NotificationStatus.getReadableStatuses());

        return notifications.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 특정 기간 이후의 알림 조회
     */
    public List<NotificationResponseDto> getRecentNotifications(Long userNo, int days) {
        UserMain user = userService.findByUserNo(userNo);

        LocalDateTime after = LocalDateTime.now().minusDays(days);
        List<NotifyPush> notifications = notifyPushRepository
                .findByUserAndCreatedAtAfterOrderByCreatedAtDesc(user, after);

        return notifications.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 알림 읽음 처리
     */
    @Transactional
    public void markAsRead(Long notifyNo, Long userNo) {
        NotifyPush notification = notifyPushRepository.findById(notifyNo)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다."));

        if (!notification.getUser().getUserNo().equals(userNo)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        notification.markAsRead();
        log.info("알림 읽음 처리 - NotifyNo: {}, UserNo: {}", notifyNo, userNo);
    }

    /**
     * 모든 알림 읽음 처리
     */
    @Transactional
    public void markAllAsRead(Long userNo) {
        UserMain user = userService.findByUserNo(userNo);

        List<NotifyPush> unreadNotifications = notifyPushRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .filter(n -> n.getStatus() == NotificationStatus.SENT)
                .collect(Collectors.toList());

        unreadNotifications.forEach(NotifyPush::markAsRead);
        log.info("모든 알림 읽음 처리 - UserNo: {}, Count: {}", userNo, unreadNotifications.size());
    }

    /**
     * 알림 삭제
     */
    @Transactional
    public void deleteNotification(Long notifyNo, Long userNo) {
        NotifyPush notification = notifyPushRepository.findById(notifyNo)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다."));

        if (!notification.getUser().getUserNo().equals(userNo)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        notifyPushRepository.delete(notification);
        log.info("알림 삭제 - NotifyNo: {}, UserNo: {}", notifyNo, userNo);
    }

    /**
     * 사용자의 모든 알림 삭제
     */
    @Transactional
    public void deleteAllNotifications(Long userNo) {
        UserMain user = userService.findByUserNo(userNo);

        List<NotifyPush> notifications = notifyPushRepository.findByUserOrderByCreatedAtDesc(user);
        notifyPushRepository.deleteAll(notifications);
        log.info("모든 알림 삭제 - UserNo: {}, Count: {}", userNo, notifications.size());
    }

    /**
     * 엔티티를 DTO로 변환
     */
    private NotificationResponseDto toResponseDto(NotifyPush notification) {
        return NotificationResponseDto.builder()
                .notifyNo(notification.getNotifyNo())
                .notificationType(notification.getNotificationType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .deepLink(notification.getDeepLink())
                .status(notification.getStatus())
                .sentAt(notification.getSentAt())
                .readAt(notification.getReadAt())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
