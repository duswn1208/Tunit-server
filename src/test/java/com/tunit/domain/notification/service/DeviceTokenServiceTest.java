package com.tunit.domain.notification.service;

import com.tunit.domain.notification.define.DeviceType;
import com.tunit.domain.notification.dto.DeviceTokenRegisterDto;
import com.tunit.domain.notification.entity.UserDeviceToken;
import com.tunit.domain.notification.repository.UserDeviceTokenRepository;
import com.tunit.domain.user.entity.UserMain;
import com.tunit.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeviceTokenService 단위 테스트")
class DeviceTokenServiceTest {

    @Mock
    private UserDeviceTokenRepository deviceTokenRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private DeviceTokenService deviceTokenService;

    private UserMain testUser;
    private DeviceTokenRegisterDto registerDto;

    @BeforeEach
    void setUp() {
        testUser = UserMain.of()
                .userNo(1L)
                .userId("test@example.com")
                .name("테스트유저")
                .build();

        registerDto = DeviceTokenRegisterDto.builder()
                .fcmToken("test-fcm-token-12345")
                .deviceType(DeviceType.WEB)
                .deviceId("device-001")
                .deviceModel("Chrome")
                .osVersion("Windows 10")
                .appVersion("1.0.0")
                .build();
    }

    @Test
    @DisplayName("새로운 디바이스 토큰 등록 성공")
    void registerNewToken_Success() {
        // given
        given(userService.findByUserNo(1L)).willReturn(testUser);
        given(deviceTokenRepository.findByFcmToken(registerDto.getFcmToken())).willReturn(Optional.empty());
        given(deviceTokenRepository.findByUserAndDeviceId(testUser, registerDto.getDeviceId())).willReturn(Optional.empty());

        UserDeviceToken savedToken = UserDeviceToken.builder()
                .tokenNo(1L)
                .user(testUser)
                .fcmToken(registerDto.getFcmToken())
                .deviceType(registerDto.getDeviceType())
                .deviceId(registerDto.getDeviceId())
                .isActive(true)
                .build();

        given(deviceTokenRepository.save(any(UserDeviceToken.class))).willReturn(savedToken);

        // when
        UserDeviceToken result = deviceTokenService.registerOrUpdateToken(1L, registerDto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTokenNo()).isEqualTo(1L);
        assertThat(result.getFcmToken()).isEqualTo(registerDto.getFcmToken());
        assertThat(result.getDeviceType()).isEqualTo(DeviceType.WEB);
        verify(deviceTokenRepository, times(1)).save(any(UserDeviceToken.class));
    }

    @Test
    @DisplayName("기존 토큰 업데이트 - 같은 사용자")
    void updateExistingToken_SameUser_Success() {
        // given
        given(userService.findByUserNo(1L)).willReturn(testUser);

        UserDeviceToken existingToken = UserDeviceToken.builder()
                .tokenNo(1L)
                .user(testUser)
                .fcmToken(registerDto.getFcmToken())
                .deviceType(DeviceType.WEB)
                .isActive(false)
                .build();

        given(deviceTokenRepository.findByFcmToken(registerDto.getFcmToken())).willReturn(Optional.of(existingToken));

        // when
        UserDeviceToken result = deviceTokenService.registerOrUpdateToken(1L, registerDto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getIsActive()).isTrue();
        verify(deviceTokenRepository, never()).save(any());
    }

    @Test
    @DisplayName("다른 사용자의 토큰이면 비활성화 후 새로 등록")
    void registerToken_DifferentUser_DeactivateAndRegisterNew() {
        // given
        UserMain otherUser = UserMain.of()
                .userNo(2L)
                .userId("other@example.com")
                .build();

        given(userService.findByUserNo(1L)).willReturn(testUser);

        UserDeviceToken otherUserToken = UserDeviceToken.builder()
                .tokenNo(1L)
                .user(otherUser)
                .fcmToken(registerDto.getFcmToken())
                .deviceType(DeviceType.WEB)
                .isActive(true)
                .build();

        given(deviceTokenRepository.findByFcmToken(registerDto.getFcmToken())).willReturn(Optional.of(otherUserToken));
        given(deviceTokenRepository.findByUserAndDeviceId(testUser, registerDto.getDeviceId())).willReturn(Optional.empty());
        given(deviceTokenRepository.save(any(UserDeviceToken.class))).willReturn(mock(UserDeviceToken.class));

        // when
        deviceTokenService.registerOrUpdateToken(1L, registerDto);

        // then
        assertThat(otherUserToken.getIsActive()).isFalse();
        verify(deviceTokenRepository, times(1)).save(any(UserDeviceToken.class));
    }

    @Test
    @DisplayName("FCM 토큰 비활성화 성공")
    void deactivateToken_Success() {
        // given
        UserDeviceToken token = UserDeviceToken.builder()
                .tokenNo(1L)
                .user(testUser)
                .fcmToken("test-token")
                .isActive(true)
                .build();

        given(deviceTokenRepository.findByFcmToken("test-token")).willReturn(Optional.of(token));

        // when
        deviceTokenService.deactivateToken("test-token");

        // then
        assertThat(token.getIsActive()).isFalse();
    }

    @Test
    @DisplayName("사용자의 모든 토큰 비활성화")
    void deactivateAllUserTokens_Success() {
        // given
        UserDeviceToken token1 = UserDeviceToken.builder()
                .tokenNo(1L)
                .user(testUser)
                .fcmToken("token-1")
                .isActive(true)
                .build();

        UserDeviceToken token2 = UserDeviceToken.builder()
                .tokenNo(2L)
                .user(testUser)
                .fcmToken("token-2")
                .isActive(true)
                .build();

        given(userService.findByUserNo(1L)).willReturn(testUser);
        given(deviceTokenRepository.findByUserAndIsActiveTrue(testUser)).willReturn(Arrays.asList(token1, token2));

        // when
        deviceTokenService.deactivateAllUserTokens(1L);

        // then
        assertThat(token1.getIsActive()).isFalse();
        assertThat(token2.getIsActive()).isFalse();
    }

    @Test
    @DisplayName("활성화된 디바이스 목록 조회")
    void getActiveDevices_Success() {
        // given
        UserDeviceToken token1 = UserDeviceToken.builder()
                .tokenNo(1L)
                .user(testUser)
                .deviceType(DeviceType.WEB)
                .isActive(true)
                .build();

        UserDeviceToken token2 = UserDeviceToken.builder()
                .tokenNo(2L)
                .user(testUser)
                .deviceType(DeviceType.ANDROID)
                .isActive(true)
                .build();

        given(userService.findByUserNo(1L)).willReturn(testUser);
        given(deviceTokenRepository.findByUserAndIsActiveTrue(testUser)).willReturn(Arrays.asList(token1, token2));

        // when
        List<UserDeviceToken> result = deviceTokenService.getActiveDevices(1L);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting("deviceType").containsExactly(DeviceType.WEB, DeviceType.ANDROID);
    }

    @Test
    @DisplayName("토큰 삭제 - 권한 확인")
    void deleteToken_UnauthorizedUser_ThrowsException() {
        // given
        UserMain otherUser = UserMain.of()
                .userNo(2L)
                .build();

        UserDeviceToken token = UserDeviceToken.builder()
                .tokenNo(1L)
                .user(otherUser)
                .build();

        given(deviceTokenRepository.findById(1L)).willReturn(Optional.of(token));

        // when & then
        assertThatThrownBy(() -> deviceTokenService.deleteToken(1L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("권한이 없습니다.");
    }

    @Test
    @DisplayName("토큰 삭제 성공")
    void deleteToken_Success() {
        // given
        UserDeviceToken token = UserDeviceToken.builder()
                .tokenNo(1L)
                .user(testUser)
                .build();

        given(deviceTokenRepository.findById(1L)).willReturn(Optional.of(token));

        // when
        deviceTokenService.deleteToken(1L, 1L);

        // then
        verify(deviceTokenRepository, times(1)).delete(token);
    }
}
