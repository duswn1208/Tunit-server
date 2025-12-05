package com.tunit.domain.notification.service;

import com.tunit.domain.notification.define.NotificationStatus;
import com.tunit.domain.notification.define.NotificationType;
import com.tunit.domain.notification.dto.NotificationResponseDto;
import com.tunit.domain.notification.entity.NotifyPush;
import com.tunit.domain.notification.repository.NotifyPushRepository;
import com.tunit.domain.user.entity.UserMain;
import com.tunit.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationService 단위 테스트")
class NotificationServiceTest {

    @Mock
    private NotifyPushRepository notifyPushRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private NotificationService notificationService;

    private UserMain testUser;
    private NotifyPush notification1;
    private NotifyPush notification2;

    @BeforeEach
    void setUp() {
        testUser = UserMain.of()
                .userNo(1L)
                .userId("test@example.com")
                .name("테스트유저")
                .build();

        notification1 = NotifyPush.builder()
                .notifyNo(1L)
                .user(testUser)
                .notificationType(NotificationType.LESSON_CONFIRMED)
                .title("수업이 확정되었습니다")
                .message("영어 회화 수업이 확정되었습니다")
                .status(NotificationStatus.SENT)
                .createdAt(LocalDateTime.now())
                .build();

        notification2 = NotifyPush.builder()
                .notifyNo(2L)
                .user(testUser)
                .notificationType(NotificationType.PAYMENT_COMPLETED)
                .title("결제가 완료되었습니다")
                .message("결제가 완료되었습니다")
                .status(NotificationStatus.SENT)
                .createdAt(LocalDateTime.now().minusHours(1))
                .build();
    }

    @Test
    @DisplayName("사용자의 알림 목록 조회 성공")
    void getUserNotifications_Success() {
        // given
        given(userService.findByUserNo(1L)).willReturn(testUser);
        given(notifyPushRepository.findByUserOrderByCreatedAtDesc(testUser))
                .willReturn(Arrays.asList(notification1, notification2));

        // when
        List<NotificationResponseDto> result = notificationService.getUserNotifications(1L);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getNotifyNo()).isEqualTo(1L);
        assertThat(result.get(0).getTitle()).isEqualTo("수업이 확정되었습니다");
        assertThat(result.get(1).getNotifyNo()).isEqualTo(2L);
    }

    @Test
    @DisplayName("특정 타입 알림 조회 성공")
    void getUserNotificationsByType_Success() {
        // given
        given(userService.findByUserNo(1L)).willReturn(testUser);
        given(notifyPushRepository.findByUserAndNotificationTypeOrderByCreatedAtDesc(
                testUser, NotificationType.LESSON_CONFIRMED))
                .willReturn(Arrays.asList(notification1));

        // when
        List<NotificationResponseDto> result = notificationService.getUserNotificationsByType(
                1L, NotificationType.LESSON_CONFIRMED);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNotificationType()).isEqualTo(NotificationType.LESSON_CONFIRMED);
    }

    @Test
    @DisplayName("읽지 않은 알림 개수 조회 성공")
    void getUnreadCount_Success() {
        // given
        given(userService.findByUserNo(1L)).willReturn(testUser);
        given(notifyPushRepository.countByUserAndStatus(testUser, NotificationStatus.SENT))
                .willReturn(3L);

        // when
        long count = notificationService.getUnreadCount(1L);

        // then
        assertThat(count).isEqualTo(3L);
    }

    @Test
    @DisplayName("최근 N일간 알림 조회 성공")
    void getRecentNotifications_Success() {
        // given
        given(userService.findByUserNo(1L)).willReturn(testUser);
        given(notifyPushRepository.findByUserAndCreatedAtAfterOrderByCreatedAtDesc(
                eq(testUser), any(LocalDateTime.class)))
                .willReturn(Arrays.asList(notification1, notification2));

        // when
        List<NotificationResponseDto> result = notificationService.getRecentNotifications(1L, 7);

        // then
        assertThat(result).hasSize(2);
        verify(notifyPushRepository).findByUserAndCreatedAtAfterOrderByCreatedAtDesc(
                eq(testUser), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("알림 읽음 처리 성공")
    void markAsRead_Success() {
        // given
        given(notifyPushRepository.findById(1L)).willReturn(Optional.of(notification1));

        // when
        notificationService.markAsRead(1L, 1L);

        // then
        assertThat(notification1.getStatus()).isEqualTo(NotificationStatus.READ);
        assertThat(notification1.getReadAt()).isNotNull();
    }

    @Test
    @DisplayName("알림 읽음 처리 - 권한 없음")
    void markAsRead_Unauthorized_ThrowsException() {
        // given
        given(notifyPushRepository.findById(1L)).willReturn(Optional.of(notification1));

        // when & then
        assertThatThrownBy(() -> notificationService.markAsRead(1L, 999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("권한이 없습니다.");
    }

    @Test
    @DisplayName("모든 알림 읽음 처리 성공")
    void markAllAsRead_Success() {
        // given
        given(userService.findByUserNo(1L)).willReturn(testUser);
        given(notifyPushRepository.findByUserOrderByCreatedAtDesc(testUser))
                .willReturn(Arrays.asList(notification1, notification2));

        // when
        notificationService.markAllAsRead(1L);

        // then
        assertThat(notification1.getStatus()).isEqualTo(NotificationStatus.READ);
        assertThat(notification2.getStatus()).isEqualTo(NotificationStatus.READ);
    }

    @Test
    @DisplayName("알림 삭제 성공")
    void deleteNotification_Success() {
        // given
        given(notifyPushRepository.findById(1L)).willReturn(Optional.of(notification1));

        // when
        notificationService.deleteNotification(1L, 1L);

        // then
        verify(notifyPushRepository, times(1)).delete(notification1);
    }

    @Test
    @DisplayName("알림 삭제 - 권한 없음")
    void deleteNotification_Unauthorized_ThrowsException() {
        // given
        given(notifyPushRepository.findById(1L)).willReturn(Optional.of(notification1));

        // when & then
        assertThatThrownBy(() -> notificationService.deleteNotification(1L, 999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("권한이 없습니다.");
    }

    @Test
    @DisplayName("모든 알림 삭제 성공")
    void deleteAllNotifications_Success() {
        // given
        given(userService.findByUserNo(1L)).willReturn(testUser);
        given(notifyPushRepository.findByUserOrderByCreatedAtDesc(testUser))
                .willReturn(Arrays.asList(notification1, notification2));

        // when
        notificationService.deleteAllNotifications(1L);

        // then
        verify(notifyPushRepository, times(1)).deleteAll(Arrays.asList(notification1, notification2));
    }
}
