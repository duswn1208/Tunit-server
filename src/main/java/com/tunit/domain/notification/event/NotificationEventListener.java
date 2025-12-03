package com.tunit.domain.notification.event;

import com.tunit.domain.notification.service.NotificationEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 알림 이벤트 리스너
 * 트랜잭션이 성공적으로 커밋된 후에만 푸시 알림을 전송합니다
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final NotificationEventService notificationEventService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleLessonConfirmed(LessonConfirmedEvent event) {
        log.info("수업 확정 이벤트 수신 (트랜잭션 커밋 후) - UserNo: {}, LessonNo: {}", event.getUserNo(), event.getLessonNo());
        notificationEventService.sendLessonConfirmedNotification(
            event.getUserNo(),
            event.getLessonTitle(),
            event.getLessonDate()
        );
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleLessonCancelled(LessonCancelledEvent event) {
        log.info("수업 취소 이벤트 수신 (트랜잭션 커밋 후) - UserNo: {}", event.getUserNo());
        notificationEventService.sendLessonCancelledNotification(
            event.getUserNo(),
            event.getLessonTitle(),
            event.getReason()
        );
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        log.info("결제 완료 이벤트 수신 (트랜잭션 커밋 후) - UserNo: {}, Amount: {}", event.getUserNo(), event.getAmount());
        notificationEventService.sendPaymentCompletedNotification(
            event.getUserNo(),
            event.getItemName(),
            event.getAmount()
        );
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleContractSigned(ContractSignedEvent event) {
        log.info("계약 체결 이벤트 수신 (트랜잭션 커밋 후) - TutorNo: {}, StudentNo: {}", event.getTutorNo(), event.getStudentNo());
        notificationEventService.sendContractSignedNotification(
            event.getTutorNo(),
            event.getStudentNo(),
            event.getContractTitle()
        );
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleReviewRequest(ReviewRequestEvent event) {
        log.info("리뷰 요청 이벤트 수신 (트랜잭션 커밋 후) - UserNo: {}", event.getUserNo());
        notificationEventService.sendReviewRequestNotification(
            event.getUserNo(),
            event.getLessonTitle()
        );
    }
}
