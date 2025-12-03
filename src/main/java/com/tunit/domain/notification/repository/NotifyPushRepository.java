package com.tunit.domain.notification.repository;

import com.tunit.domain.notification.entity.NotifyPush;
import com.tunit.domain.notification.define.NotificationStatus;
import com.tunit.domain.notification.define.NotificationType;
import com.tunit.domain.user.entity.UserMain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotifyPushRepository extends JpaRepository<NotifyPush, Long> {

    /**
     * 사용자의 알림 목록 조회 (최신순)
     */
    List<NotifyPush> findByUserOrderByCreatedAtDesc(UserMain user);

    /**
     * 사용자의 읽지 않은 알림 개수 조회
     */
    long countByUserAndStatus(UserMain user, NotificationStatus status);

    /**
     * 사용자의 특정 타입 알림 조회
     */
    List<NotifyPush> findByUserAndNotificationTypeOrderByCreatedAtDesc(UserMain user, NotificationType notificationType);

    /**
     * 전송 대기 중인 알림 조회
     */
    List<NotifyPush> findByStatusOrderByCreatedAtAsc(NotificationStatus status);

    /**
     * 특정 기간 이후 생성된 사용자의 알림 조회
     */
    List<NotifyPush> findByUserAndCreatedAtAfterOrderByCreatedAtDesc(UserMain user, LocalDateTime after);

    /**
     * 전송 실패한 알림 중 재시도 가능한 것들 조회
     */
    List<NotifyPush> findByStatusAndCreatedAtAfter(NotificationStatus status, LocalDateTime after);

    long countByUserAndStatusIn(UserMain user, List<NotificationStatus> notificationStatuses);

    List<NotifyPush> findByUserAndStatusInOrderByCreatedAtDesc(UserMain user, List<NotificationStatus> readableStatuses);
}

