package com.tunit.domain.notification.scheduler;

import com.tunit.domain.notification.service.NotificationEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 알림 스케줄러
 * 정해진 시간에 자동으로 알림을 보냅니다
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final NotificationEventService notificationEventService;
    // private final LessonService lessonService;

    /**
     * 매 시간마다 1시간 후 시작하는 수업이 있는지 확인하고 알림 전송
     */
    @Scheduled(cron = "0 0 * * * *")  // 매 시간 정각
    public void sendLessonReminders() {
        log.info("수업 알림 스케줄러 시작");

        // 1시간 후 시작하는 수업 조회
        // List<Lesson> upcomingLessons = lessonService.findLessonsStartingInOneHour();

        // 각 수업에 대해 알림 전송
        // upcomingLessons.forEach(lesson -> {
        //     notificationEventService.sendLessonReminderNotification(
        //         lesson.getUserNo(),
        //         lesson.getTitle(),
        //         lesson.getStartTime().toString()
        //     );
        // });

        log.info("수업 알림 스케줄러 완료");
    }

    /**
     * 매일 오전 9시에 완료된 수업에 대해 리뷰 요청 알림 전송
     */
    @Scheduled(cron = "0 0 9 * * *")  // 매일 오전 9시
    public void sendReviewRequests() {
        log.info("리뷰 요청 알림 스케줄러 시작");

        // 전날 완료된 수업 중 리뷰가 없는 수업 조회
        // List<Lesson> completedLessons = lessonService.findCompletedLessonsWithoutReview();

        // 각 수업에 대해 리뷰 요청 알림 전송
        // completedLessons.forEach(lesson -> {
        //     notificationEventService.sendReviewRequestNotification(
        //         lesson.getUserNo(),
        //         lesson.getTitle()
        //     );
        // });

        log.info("리뷰 요청 알림 스케줄러 완료");
    }
}

