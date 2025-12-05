package com.tunit.domain.notification.event;

import com.tunit.domain.notification.service.NotificationEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationEventListener 단위 테스트")
class NotificationEventListenerTest {

    @Mock
    private NotificationEventService notificationEventService;

    @InjectMocks
    private NotificationEventListener notificationEventListener;

    @Test
    @DisplayName("수업 확정 이벤트 처리")
    void handleLessonConfirmed_Success() {
        // given
        LessonConfirmedEvent event = new LessonConfirmedEvent(
                this, 1L, 100L, "영어 회화", "2025-12-05 14:00"
        );

        // when
        notificationEventListener.handleLessonConfirmed(event);

        // then
        verify(notificationEventService, times(1))
                .sendLessonConfirmedNotification(1L, "영어 회화", "2025-12-05 14:00");
    }

    @Test
    @DisplayName("수업 취소 이벤트 처리")
    void handleLessonCancelled_Success() {
        // given
        LessonCancelledEvent event = new LessonCancelledEvent(
                this, 1L, "영어 회화", "개인 사정"
        );

        // when
        notificationEventListener.handleLessonCancelled(event);

        // then
        verify(notificationEventService, times(1))
                .sendLessonCancelledNotification(1L, "영어 회화", "개인 사정");
    }

    @Test
    @DisplayName("결제 완료 이벤트 처리")
    void handlePaymentCompleted_Success() {
        // given
        PaymentCompletedEvent event = new PaymentCompletedEvent(
                this, 1L, "수업료", 50000
        );

        // when
        notificationEventListener.handlePaymentCompleted(event);

        // then
        verify(notificationEventService, times(1))
                .sendPaymentCompletedNotification(1L, "수업료", 50000);
    }

    @Test
    @DisplayName("계약 체결 이벤트 처리")
    void handleContractSigned_Success() {
        // given
        ContractSignedEvent event = new ContractSignedEvent(
                this, 1L, 2L, "2025년 1분기 계약"
        );

        // when
        notificationEventListener.handleContractSigned(event);

        // then
        verify(notificationEventService, times(1))
                .sendContractSignedNotification(1L, 2L, "2025년 1분기 계약");
    }

    @Test
    @DisplayName("리뷰 요청 이벤트 처리")
    void handleReviewRequest_Success() {
        // given
        ReviewRequestEvent event = new ReviewRequestEvent(
                this, 1L, "영어 회화"
        );

        // when
        notificationEventListener.handleReviewRequest(event);

        // then
        verify(notificationEventService, times(1))
                .sendReviewRequestNotification(1L, "영어 회화");
    }
}

