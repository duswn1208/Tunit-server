package com.tunit.domain.notification.entity;

import com.tunit.domain.notification.define.NotificationStatus;
import com.tunit.domain.notification.define.NotificationType;
import com.tunit.domain.user.entity.UserMain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("NotifyPush 엔티티 테스트")
class NotifyPushTest {

    private UserMain testUser;
    private NotifyPush notification;

    @BeforeEach
    void setUp() {
        testUser = UserMain.of()
                .userNo(1L)
                .userId("test@example.com")
                .build();

        notification = NotifyPush.builder()
                .notifyNo(1L)
                .user(testUser)
                .notificationType(NotificationType.LESSON_CONFIRMED)
                .title("수업이 확정되었습니다")
                .message("영어 회화 수업이 확정되었습니다")
                .status(NotificationStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("알림 전송 성공 처리")
    void markAsSent_Success() {
        // given
        String messageId = "fcm-message-123";
        LocalDateTime beforeSent = LocalDateTime.now();

        // when
        notification.markAsSent(messageId);

        // then
        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.SENT);
        assertThat(notification.getFcmMessageId()).isEqualTo(messageId);
        assertThat(notification.getSentAt()).isAfter(beforeSent);
        assertThat(notification.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("알림 전송 실패 처리")
    void markAsFailed_Success() {
        // given
        String errorMessage = "FCM 전송 실패";

        // when
        notification.markAsFailed(errorMessage);

        // then
        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.FAILED);
        assertThat(notification.getErrorMessage()).isEqualTo(errorMessage);
        assertThat(notification.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("알림 읽음 처리 - 전송된 알림만 가능")
    void markAsRead_WhenSent_Success() {
        // given
        notification.markAsSent("message-id");
        LocalDateTime beforeRead = LocalDateTime.now();

        // when
        notification.markAsRead();

        // then
        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.READ);
        assertThat(notification.getReadAt()).isAfter(beforeRead);
        assertThat(notification.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("알림 읽음 처리 - 전송되지 않은 알림은 읽음 처리 불가")
    void markAsRead_WhenNotSent_NoChange() {
        // given
        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.PENDING);

        // when
        notification.markAsRead();

        // then
        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.PENDING);
        assertThat(notification.getReadAt()).isNull();
    }

    @Test
    @DisplayName("재전송을 위한 상태 초기화")
    void resetForRetry_Success() {
        // given
        notification.markAsFailed("전송 실패");
        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.FAILED);

        // when
        notification.resetForRetry();

        // then
        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.PENDING);
        assertThat(notification.getErrorMessage()).isNull();
        assertThat(notification.getUpdatedAt()).isNotNull();
    }
}
