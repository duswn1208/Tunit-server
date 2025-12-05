package com.tunit.domain.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.tunit.domain.notification.define.NotificationStatus;
import com.tunit.domain.notification.define.NotificationType;
import com.tunit.domain.notification.dto.PushNotificationDto;
import com.tunit.domain.notification.entity.NotifyPush;
import com.tunit.domain.notification.entity.UserDeviceToken;
import com.tunit.domain.notification.repository.NotifyPushRepository;
import com.tunit.domain.notification.repository.UserDeviceTokenRepository;
import com.tunit.domain.user.entity.UserMain;
import com.tunit.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PushNotificationService 단위 테스트")
class PushNotificationServiceTest {

    @Mock
    private NotifyPushRepository notifyPushRepository;

    @Mock
    private UserDeviceTokenRepository deviceTokenRepository;

    @Mock
    private UserService userService;

    @Mock
    private FirebaseMessaging firebaseMessaging;

    @InjectMocks
    private PushNotificationService pushNotificationService;

    private UserMain testUser;
    private PushNotificationDto pushDto;
    private UserDeviceToken deviceToken;
    private NotifyPush savedNotification;

    @BeforeEach
    void setUp() {
        testUser = UserMain.of()
                .userNo(1L)
                .userId("test@example.com")
                .name("테스트유저")
                .build();

        pushDto = PushNotificationDto.builder()
                .userNo(1L)
                .notificationType(NotificationType.LESSON_CONFIRMED)
                .title("수업이 확정되었습니다")
                .message("영어 회화 수업이 확정되었습니다")
                .deepLink("/lessons/1")
                .build();

        deviceToken = UserDeviceToken.builder()
                .tokenNo(1L)
                .user(testUser)
                .fcmToken("test-fcm-token")
                .isActive(true)
                .build();

        savedNotification = NotifyPush.builder()
                .notifyNo(1L)
                .user(testUser)
                .notificationType(pushDto.getNotificationType())
                .title(pushDto.getTitle())
                .message(pushDto.getMessage())
                .status(NotificationStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("푸시 알림 전송 성공")
    void sendPushNotification_Success() {
        // given
        given(userService.findByUserNo(1L)).willReturn(testUser);
        given(notifyPushRepository.save(any(NotifyPush.class))).willReturn(savedNotification);
        given(deviceTokenRepository.findByUserAndIsActiveTrue(testUser))
                .willReturn(Arrays.asList(deviceToken));

        // FirebaseMessaging mock
        try (MockedStatic<FirebaseMessaging> mockedFirebase = mockStatic(FirebaseMessaging.class)) {
            FirebaseMessaging mockMessaging = mock(FirebaseMessaging.class);
            mockedFirebase.when(FirebaseMessaging::getInstance).thenReturn(mockMessaging);

            try {
                given(mockMessaging.send(any(Message.class))).willReturn("message-id-123");
            } catch (FirebaseMessagingException e) {
                throw new RuntimeException(e);
            }

            // when
            pushNotificationService.sendPushNotification(pushDto);

            // then
            verify(notifyPushRepository, times(1)).save(any(NotifyPush.class));
            assertThat(savedNotification.getStatus()).isEqualTo(NotificationStatus.SENT);
        }
    }

    @Test
    @DisplayName("푸시 알림 전송 실패 - 활성 디바이스 없음")
    void sendPushNotification_NoActiveDevices_Failed() {
        // given
        given(userService.findByUserNo(1L)).willReturn(testUser);
        given(notifyPushRepository.save(any(NotifyPush.class))).willReturn(savedNotification);
        given(deviceTokenRepository.findByUserAndIsActiveTrue(testUser))
                .willReturn(Collections.emptyList());

        // when
        pushNotificationService.sendPushNotification(pushDto);

        // then
        assertThat(savedNotification.getStatus()).isEqualTo(NotificationStatus.FAILED);
        assertThat(savedNotification.getErrorMessage()).contains("활성화된 디바이스 토큰이 없습니다");
    }

    @Test
    @DisplayName("일괄 푸시 알림 전송")
    void sendBulkNotification_Success() {
        // given
        List<Long> userNos = Arrays.asList(1L, 2L, 3L);

        given(userService.findByUserNo(anyLong())).willReturn(testUser);
        given(notifyPushRepository.save(any(NotifyPush.class))).willReturn(savedNotification);
        given(deviceTokenRepository.findByUserAndIsActiveTrue(any(UserMain.class)))
                .willReturn(Arrays.asList(deviceToken));

        try (MockedStatic<FirebaseMessaging> mockedFirebase = mockStatic(FirebaseMessaging.class)) {
            FirebaseMessaging mockMessaging = mock(FirebaseMessaging.class);
            mockedFirebase.when(FirebaseMessaging::getInstance).thenReturn(mockMessaging);

            try {
                given(mockMessaging.send(any(Message.class))).willReturn("message-id-123");
            } catch (FirebaseMessagingException e) {
                throw new RuntimeException(e);
            }

            // when
            pushNotificationService.sendBulkNotification(userNos, pushDto);

            // then
            verify(notifyPushRepository, times(3)).save(any(NotifyPush.class));
        }
    }

    @Test
    @DisplayName("전송 실패한 알림 재전송")
    void retryFailedNotifications_Success() {
        // given
        NotifyPush failedNotification = NotifyPush.builder()
                .notifyNo(1L)
                .user(testUser)
                .notificationType(NotificationType.LESSON_CONFIRMED)
                .title("수업 확정")
                .message("테스트")
                .status(NotificationStatus.FAILED)
                .build();

        given(notifyPushRepository.findByStatusOrderByCreatedAtAsc(NotificationStatus.FAILED))
                .willReturn(Arrays.asList(failedNotification));
        given(deviceTokenRepository.findByUserAndIsActiveTrue(testUser))
                .willReturn(Arrays.asList(deviceToken));

        try (MockedStatic<FirebaseMessaging> mockedFirebase = mockStatic(FirebaseMessaging.class)) {
            FirebaseMessaging mockMessaging = mock(FirebaseMessaging.class);
            mockedFirebase.when(FirebaseMessaging::getInstance).thenReturn(mockMessaging);

            try {
                given(mockMessaging.send(any(Message.class))).willReturn("message-id-123");
            } catch (FirebaseMessagingException e) {
                throw new RuntimeException(e);
            }

            // when
            pushNotificationService.retryFailedNotifications();

            // then
            assertThat(failedNotification.getStatus()).isIn(NotificationStatus.PENDING, NotificationStatus.SENT);
        }
    }
}
