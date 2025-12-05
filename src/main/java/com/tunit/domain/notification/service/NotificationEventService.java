package com.tunit.domain.notification.service;

import com.tunit.domain.notification.define.NotificationType;
import com.tunit.domain.notification.dto.PushNotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 비즈니스 이벤트에 따른 알림 전송 헬퍼 서비스
 * 다른 도메인에서 쉽게 알림을 보낼 수 있도록 도와줍니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationEventService {

    private final PushNotificationService pushNotificationService;

    /**
     * 수업 예약 확정 알림
     */
    public void sendLessonConfirmedNotification(Long userNo, String lessonTitle, String lessonDate) {
        PushNotificationDto dto = PushNotificationDto.builder()
                .userNo(userNo)
                .notificationType(NotificationType.LESSON_CONFIRMED)
                .title("수업이 확정되었습니다")
                .message(String.format("%s 수업이 %s에 확정되었습니다.", lessonTitle, lessonDate))
                .deepLink("/lessons/" + userNo)
                .build();

        pushNotificationService.sendPushNotification(dto);
        log.info("수업 확정 알림 전송 - UserNo: {}, LessonTitle: {}", userNo, lessonTitle);
    }

    /**
     * 수업 취소 알림
     */
    public void sendLessonCancelledNotification(Long userNo, String lessonTitle, String reason) {
        PushNotificationDto dto = PushNotificationDto.builder()
                .userNo(userNo)
                .notificationType(NotificationType.LESSON_CANCELLED)
                .title("수업이 취소되었습니다")
                .message(String.format("%s 수업이 취소되었습니다. 사유: %s", lessonTitle, reason))
                .deepLink("/lessons")
                .build();

        pushNotificationService.sendPushNotification(dto);
        log.info("수업 취소 알림 전송 - UserNo: {}, Reason: {}", userNo, reason);
    }

    /**
     * 수업 시작 1시간 전 알림
     */
    public void sendLessonReminderNotification(Long userNo, String lessonTitle, String startTime) {
        PushNotificationDto dto = PushNotificationDto.builder()
                .userNo(userNo)
                .notificationType(NotificationType.LESSON_REMINDER)
                .title("곧 수업이 시작됩니다")
                .message(String.format("%s 수업이 1시간 후 시작됩니다.", lessonTitle))
                .deepLink("/lessons/" + userNo)
                .build();

        pushNotificationService.sendPushNotification(dto);
        log.info("수업 알림 전송 - UserNo: {}, StartTime: {}", userNo, startTime);
    }

    /**
     * 결제 완료 알림
     */
    public void sendPaymentCompletedNotification(Long userNo, String itemName, int amount) {
        PushNotificationDto dto = PushNotificationDto.builder()
                .userNo(userNo)
                .notificationType(NotificationType.PAYMENT_COMPLETED)
                .title("결제가 완료되었습니다")
                .message(String.format("%s 결제가 완료되었습니다. (금액: %,d원)", itemName, amount))
                .deepLink("/payments")
                .build();

        pushNotificationService.sendPushNotification(dto);
        log.info("결제 완료 알림 전송 - UserNo: {}, Amount: {}", userNo, amount);
    }

    /**
     * 계약 체결 알림
     */
    public void sendContractSignedNotification(Long tutorNo, Long studentNo, String contractTitle) {
        // 튜터에게 알림
        PushNotificationDto tutorDto = PushNotificationDto.builder()
                .userNo(tutorNo)
                .notificationType(NotificationType.CONTRACT_SIGNED)
                .title("새로운 계약이 체결되었습니다")
                .message(String.format("%s 계약이 체결되었습니다.", contractTitle))
                .deepLink("/contracts")
                .build();
        pushNotificationService.sendPushNotification(tutorDto);

        // 학생에게 알림
        PushNotificationDto studentDto = PushNotificationDto.builder()
                .userNo(studentNo)
                .notificationType(NotificationType.CONTRACT_SIGNED)
                .title("계약이 체결되었습니다")
                .message(String.format("%s 계약이 체결되었습니다.", contractTitle))
                .deepLink("/contracts")
                .build();
        pushNotificationService.sendPushNotification(studentDto);

        log.info("계약 체결 알림 전송 - TutorNo: {}, StudentNo: {}", tutorNo, studentNo);
    }

    /**
     * 리뷰 요청 알림
     */
    public void sendReviewRequestNotification(Long userNo, String lessonTitle) {
        PushNotificationDto dto = PushNotificationDto.builder()
                .userNo(userNo)
                .notificationType(NotificationType.REVIEW_REQUEST)
                .title("수업 리뷰를 남겨주세요")
                .message(String.format("%s 수업은 어떠셨나요? 리뷰를 남겨주세요.", lessonTitle))
                .deepLink("/reviews/write")
                .build();

        pushNotificationService.sendPushNotification(dto);
        log.info("리뷰 요청 알림 전송 - UserNo: {}", userNo);
    }

    /**
     * 시스템 공지 알림 (전체 사용자)
     */
    public void sendSystemNoticeToAll(String title, String message) {
        // 실제로는 활성 사용자 목록을 조회해서 일괄 전송
        PushNotificationDto dto = PushNotificationDto.builder()
                .notificationType(NotificationType.SYSTEM_NOTICE)
                .title(title)
                .message(message)
                .deepLink("/notices")
                .build();

        // TODO: 전체 사용자에게 일괄 전송 로직 구현
        log.info("시스템 공지 알림 전송 - Title: {}", title);
    }
}

