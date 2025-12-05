package com.tunit.domain.notification.entity;

import com.tunit.domain.notification.define.DeviceType;
import com.tunit.domain.user.entity.UserMain;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 디바이스 토큰 엔티티
 * 웹/앱 푸시 알림을 위한 FCM 토큰 관리
 */
@Entity
@Table(name = "user_device_token",
    indexes = {
        @Index(name = "idx_user_device", columnList = "user_no, deviceId"),
        @Index(name = "idx_fcm_token", columnList = "fcmToken")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_fcm_token", columnNames = "fcmToken")
    })
@NoArgsConstructor
@Getter
public class UserDeviceToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", nullable = false)
    private UserMain user;

    @Column(nullable = false, length = 500)
    private String fcmToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DeviceType deviceType;

    @Column(length = 100)
    private String deviceId;  // 디바이스 고유 ID (선택)

    @Column(length = 500)
    private String deviceModel;  // 디바이스 모델명 (선택)

    @Column(length = 50)
    private String osVersion;  // OS 버전 (선택)

    @Column(length = 50)
    private String appVersion;  // 앱 버전 (선택)

    @Column(nullable = false)
    private Boolean isActive = true;  // 토큰 활성 상태

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime lastUsedAt;  // 마지막 사용 시간

    @Builder
    public UserDeviceToken(Long tokenNo, UserMain user, String fcmToken, DeviceType deviceType,
                          String deviceId, String deviceModel, String osVersion, String appVersion,
                          Boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt,
                          LocalDateTime lastUsedAt) {
        this.tokenNo = tokenNo;
        this.user = user;
        this.fcmToken = fcmToken;
        this.deviceType = deviceType;
        this.deviceId = deviceId;
        this.deviceModel = deviceModel;
        this.osVersion = osVersion;
        this.appVersion = appVersion;
        this.isActive = isActive != null ? isActive : true;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lastUsedAt = lastUsedAt;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.lastUsedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * FCM 토큰 업데이트
     */
    public void updateFcmToken(String newToken) {
        this.fcmToken = newToken;
        this.updatedAt = LocalDateTime.now();
        this.lastUsedAt = LocalDateTime.now();
    }

    /**
     * 디바이스 정보 업데이트
     */
    public void updateDeviceInfo(com.tunit.domain.notification.dto.DeviceTokenRegisterDto dto) {
        this.deviceType = dto.getDeviceType();
        this.deviceModel = dto.getDeviceModel();
        this.osVersion = dto.getOsVersion();
        this.appVersion = dto.getAppVersion();
        this.updatedAt = LocalDateTime.now();
        this.lastUsedAt = LocalDateTime.now();
    }

    /**
     * 토큰 비활성화
     */
    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 토큰 활성화
     */
    public void activate() {
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
        this.lastUsedAt = LocalDateTime.now();
    }

    /**
     * 마지막 사용 시간 갱신
     */
    public void updateLastUsedAt() {
        this.lastUsedAt = LocalDateTime.now();
    }
}
