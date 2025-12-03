package com.tunit.domain.notification.annotation;

import com.tunit.domain.notification.define.NotificationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 메서드 실행 후 자동으로 푸시 알림을 전송하는 어노테이션
 *
 * 사용 예시:
 * @SendNotification(
 *     type = NotificationType.LESSON_CONFIRMED,
 *     title = "수업이 확정되었습니다",
 *     message = "#{#lesson.title} 수업이 #{#lesson.startTime}에 확정되었습니다",
 *     userNoField = "#lesson.studentNo"
 * )
 * public Lesson confirmLesson(Long lessonNo) { ... }
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SendNotification {

    /**
     * 알림 타입
     */
    NotificationType type();

    /**
     * 알림 제목 (SpEL 표현식 사용 가능)
     */
    String title();

    /**
     * 알림 메시지 (SpEL 표현식 사용 가능)
     */
    String message();

    /**
     * 사용자 번호를 가져올 필드 (SpEL 표현식)
     * 예: "#result.userNo", "#userNo", "#lesson.studentNo"
     */
    String userNoField();

    /**
     * 딥링크 (선택사항, SpEL 표현식 사용 가능)
     */
    String deepLink() default "";

    /**
     * 이미지 URL (선택사항, SpEL 표현식 사용 가능)
     */
    String imageUrl() default "";
}

