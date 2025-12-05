package com.tunit.domain.notification.entity;

import com.tunit.domain.notification.define.NotificationStatus;
import com.tunit.domain.notification.define.NotificationType;
import com.tunit.domain.user.entity.UserMain;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 푸시 알림 엔티티
 * 웹/앱 공통 푸시 알림 이력 관리
 */
@Entity
@Table(name = "notify_push")
@NoArgsConstructor
@Getter
public class NotifyPush {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notifyNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", nullable = false)
    private UserMain user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NotificationType notificationType;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;  // 알림 이미지 URL (선택)

    @Column(length = 500)
    private String deepLink;  // 딥링크 URL (클릭 시 이동할 화면)

    @Column(columnDefinition = "TEXT")
    private String data;  // 추가 데이터 (JSON 형식)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationStatus status;

    @Column(length = 500)
    private String fcmMessageId;  // FCM 전송 결과 메시지 ID

    @Column(columnDefinition = "TEXT")
    private String errorMessage;  // 전송 실패 시 에러 메시지

    private LocalDateTime sentAt;  // 전송 시각

    private LocalDateTime readAt;  // 읽은 시각

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    public NotifyPush(Long notifyNo, UserMain user, NotificationType notificationType,
                     String title, String message, String imageUrl, String deepLink,
                     String data, NotificationStatus status, String fcmMessageId,
                     String errorMessage, LocalDateTime sentAt, LocalDateTime readAt,
                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.notifyNo = notifyNo;
        this.user = user;
        this.notificationType = notificationType;
        this.title = title;
        this.message = message;
        this.imageUrl = imageUrl;
        this.deepLink = deepLink;
        this.data = data;
        this.status = status != null ? status : NotificationStatus.PENDING;
        this.fcmMessageId = fcmMessageId;
        this.errorMessage = errorMessage;
        this.sentAt = sentAt;
        this.readAt = readAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 알림 전송 성공 처리
     */
    public void markAsSent(String fcmMessageId) {
        this.status = NotificationStatus.SENT;
        this.fcmMessageId = fcmMessageId;
        this.sentAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 알림 전송 실패 처리
     */
    public void markAsFailed(String errorMessage) {
        this.status = NotificationStatus.FAILED;
        this.errorMessage = errorMessage;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 알림 읽음 처리
     */
    public void markAsRead() {
        if (this.status == NotificationStatus.SENT || this.status == NotificationStatus.PENDING) {
            this.status = NotificationStatus.READ;
            this.readAt = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * 알림 재전송을 위한 상태 초기화
     */
    public void resetForRetry() {
        this.status = NotificationStatus.PENDING;
        this.errorMessage = null;
        this.updatedAt = LocalDateTime.now();
    }
}
