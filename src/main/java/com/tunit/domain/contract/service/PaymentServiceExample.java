package com.tunit.domain.contract.service;

import com.tunit.domain.notification.service.NotificationEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 결제 서비스 예시
 * 결제 완료 시 자동으로 알림을 보내는 방법
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceExample {

    // 기존 결제 관련 의존성들...
    // private final PaymentRepository paymentRepository;

    // 알림 서비스 주입
    private final NotificationEventService notificationEventService;

    /**
     * 결제 처리 (예시)
     */
    public void processPayment(Long userNo, String itemName, int amount) {
        // 1. 결제 처리 비즈니스 로직
        // Payment payment = Payment.builder()
        //     .userNo(userNo)
        //     .amount(amount)
        //     .status(PaymentStatus.COMPLETED)
        //     .build();
        // paymentRepository.save(payment);

        // 2. 결제 완료 알림 자동 전송
        notificationEventService.sendPaymentCompletedNotification(
            userNo,
            itemName,
            amount
        );

        // 사용자는 자동으로 "결제가 완료되었습니다" 푸시 알림을 받게 됩니다!
    }

    /**
     * 계약 체결 시 (예시)
     */
    public void signContract(Long tutorNo, Long studentNo, String contractTitle) {
        // 1. 계약 체결 비즈니스 로직
        // ...

        // 2. 튜터와 학생 모두에게 알림 전송
        notificationEventService.sendContractSignedNotification(
            tutorNo,
            studentNo,
            contractTitle
        );
    }
}

