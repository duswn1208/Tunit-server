package com.tunit.domain.notification.entity;

import com.tunit.domain.notification.define.DeviceType;
import com.tunit.domain.user.entity.UserMain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserDeviceToken 엔티티 테스트")
class UserDeviceTokenTest {

    private UserMain testUser;
    private UserDeviceToken deviceToken;

    @BeforeEach
    void setUp() {
        testUser = UserMain.of()
                .userNo(1L)
                .userId("test@example.com")
                .build();

        deviceToken = UserDeviceToken.builder()
                .tokenNo(1L)
                .user(testUser)
                .fcmToken("test-token")
                .deviceType(DeviceType.WEB)
                .deviceId("device-001")
                .isActive(true)
                .build();
    }

    @Test
    @DisplayName("FCM 토큰 업데이트")
    void updateFcmToken_Success() {
        // given
        String newToken = "new-fcm-token";
        LocalDateTime beforeUpdate = LocalDateTime.now();

        // when
        deviceToken.updateFcmToken(newToken);

        // then
        assertThat(deviceToken.getFcmToken()).isEqualTo(newToken);
        assertThat(deviceToken.getUpdatedAt()).isNotNull();
        assertThat(deviceToken.getLastUsedAt()).isAfter(beforeUpdate);
    }

    @Test
    @DisplayName("토큰 비활성화")
    void deactivate_Success() {
        // given
        assertThat(deviceToken.getIsActive()).isTrue();

        // when
        deviceToken.deactivate();

        // then
        assertThat(deviceToken.getIsActive()).isFalse();
        assertThat(deviceToken.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("토큰 활성화")
    void activate_Success() {
        // given
        deviceToken.deactivate();
        assertThat(deviceToken.getIsActive()).isFalse();

        // when
        deviceToken.activate();

        // then
        assertThat(deviceToken.getIsActive()).isTrue();
        assertThat(deviceToken.getUpdatedAt()).isNotNull();
        assertThat(deviceToken.getLastUsedAt()).isNotNull();
    }

    @Test
    @DisplayName("마지막 사용 시간 갱신")
    void updateLastUsedAt_Success() {
        // given
        LocalDateTime beforeUpdate = LocalDateTime.now();

        // when
        deviceToken.updateLastUsedAt();

        // then
        assertThat(deviceToken.getLastUsedAt()).isAfter(beforeUpdate);
    }
}
