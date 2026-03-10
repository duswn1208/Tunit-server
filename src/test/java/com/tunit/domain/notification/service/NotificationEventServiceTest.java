package com.tunit.domain.notification.service;

import com.tunit.domain.notification.define.NotificationType;
import com.tunit.domain.notification.dto.PushNotificationDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationEventService 단위 테스트")
class NotificationEventServiceTest {

    @Mock
    private PushNotificationService pushNotificationService;

    @InjectMocks
    private NotificationEventService notificationEventService;

    @Nested
    @DisplayName("sendPaymentAccountNotification()")
    class SendPaymentAccountNotification {

        @Test
        @DisplayName("알림 타입이 PAYMENT_ACCOUNT_INFO이고 메시지에 튜터명과 금액이 포함된다")
        void send_correctTypeAndMessage() {
            // given
            Long studentNo = 10L;
            Long contractNo = 42L;
            String tutorName = "홍길동";
            int totalPrice = 120000;
            String bankName = "국민은행";
            String accountNumber = "123-456-789012";
            String accountHolder = "홍길동";

            // when
            notificationEventService.sendPaymentAccountNotification(
                    studentNo, contractNo, tutorName, totalPrice,
                    bankName, accountNumber, accountHolder
            );

            // then
            ArgumentCaptor<PushNotificationDto> captor = ArgumentCaptor.forClass(PushNotificationDto.class);
            verify(pushNotificationService).sendPushNotification(captor.capture());

            PushNotificationDto dto = captor.getValue();
            assertThat(dto.getUserNo()).isEqualTo(studentNo);
            assertThat(dto.getNotificationType()).isEqualTo(NotificationType.PAYMENT_ACCOUNT_INFO);
            assertThat(dto.getMessage()).contains(tutorName);
            assertThat(dto.getMessage()).contains("120,000원");
        }

        @Test
        @DisplayName("FCM data JSON에 contractNo, bankName, accountNumber, accountHolder, totalPrice가 포함된다")
        void send_dataJsonContainsAllFields() {
            // when
            notificationEventService.sendPaymentAccountNotification(
                    10L, 42L, "홍길동", 120000,
                    "국민은행", "123-456-789012", "홍길동"
            );

            // then
            ArgumentCaptor<PushNotificationDto> captor = ArgumentCaptor.forClass(PushNotificationDto.class);
            verify(pushNotificationService).sendPushNotification(captor.capture());

            String data = captor.getValue().getData();
            assertThat(data).contains("\"contractNo\":42");
            assertThat(data).contains("\"bankName\":\"국민은행\"");
            assertThat(data).contains("\"accountNumber\":\"123-456-789012\"");
            assertThat(data).contains("\"accountHolder\":\"홍길동\"");
            assertThat(data).contains("\"totalPrice\":120000");
        }

        @Test
        @DisplayName("deepLink에 contractNo가 포함된다")
        void send_deepLinkContainsContractNo() {
            // when
            notificationEventService.sendPaymentAccountNotification(
                    10L, 42L, "홍길동", 120000,
                    "국민은행", "123-456-789012", "홍길동"
            );

            // then
            ArgumentCaptor<PushNotificationDto> captor = ArgumentCaptor.forClass(PushNotificationDto.class);
            verify(pushNotificationService).sendPushNotification(captor.capture());

            assertThat(captor.getValue().getDeepLink()).contains("42");
        }
    }
}
