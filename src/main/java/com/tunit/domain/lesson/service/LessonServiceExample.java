package com.tunit.domain.lesson.service;

import com.tunit.domain.notification.service.NotificationEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 수업 서비스 예시
 * 알림을 어떻게 통합하는지 보여주는 예제
 */
@Service
@RequiredArgsConstructor
@Transactional
public class LessonServiceExample {

    // 기존 레슨 관련 의존성들...
    // private final LessonRepository lessonRepository;

    // 알림 서비스 주입
    private final NotificationEventService notificationEventService;

    /**
     * 수업 예약 확정 (예시)
     */
    public void confirmLesson(Long lessonNo, Long userNo) {
        // 1. 수업 확정 비즈니스 로직
        // Lesson lesson = lessonRepository.findById(lessonNo)...
        // lesson.confirm();
        // lessonRepository.save(lesson);

        // 2. 알림 자동 전송 (한 줄로 끝!)
        notificationEventService.sendLessonConfirmedNotification(
            userNo,
            "영어 회화 수업",  // lesson.getTitle()
            "2025-12-05 14:00"  // lesson.getStartTime()
        );

        // 끝! 클라이언트는 자동으로 푸시 알림 받음
    }

    /**
     * 수업 취소 (예시)
     */
    public void cancelLesson(Long lessonNo, Long userNo, String reason) {
        // 1. 수업 취소 비즈니스 로직
        // ...

        // 2. 알림 자동 전송
        notificationEventService.sendLessonCancelledNotification(
            userNo,
            "영어 회화 수업",
            reason
        );
    }
}

