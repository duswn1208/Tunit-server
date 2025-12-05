package com.tunit.domain.notification.service;

import com.tunit.domain.notification.dto.DeviceTokenRegisterDto;
import com.tunit.domain.notification.entity.UserDeviceToken;
import com.tunit.domain.notification.repository.UserDeviceTokenRepository;
import com.tunit.domain.user.entity.UserMain;
import com.tunit.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 디바이스 토큰 관리 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DeviceTokenService {

    private final UserDeviceTokenRepository deviceTokenRepository;
    private final UserService userService;

    /**
     * 디바이스 토큰 등록 또는 업데이트
     */
    public UserDeviceToken registerOrUpdateToken(Long userNo, DeviceTokenRegisterDto dto) {
        UserMain user = userService.findByUserNo(userNo);

        // 동일한 FCM 토큰이 이미 존재하는지 확인
        Optional<UserDeviceToken> existingToken = deviceTokenRepository.findByFcmToken(dto.getFcmToken());

        if (existingToken.isPresent()) {
            UserDeviceToken token = existingToken.get();

            // 다른 사용자의 토큰이면 비활성화
            if (!token.getUser().getUserNo().equals(userNo)) {
                token.deactivate();
                log.info("다른 사용자의 토큰 비활성화 - UserNo: {}, TokenNo: {}",
                        token.getUser().getUserNo(), token.getTokenNo());
            } else {
                // 같은 사용자의 토큰이면 업데이트
                token.updateFcmToken(dto.getFcmToken());
                token.activate();
                log.info("디바이스 토큰 업데이트 - UserNo: {}, DeviceType: {}", userNo, dto.getDeviceType());
                return token;
            }
        }

        // deviceId가 있으면 해당 디바이스의 기존 토큰 확인
        if (dto.getDeviceId() != null) {
            Optional<UserDeviceToken> existingDeviceToken =
                    deviceTokenRepository.findByUserAndDeviceId(user, dto.getDeviceId());

            if (existingDeviceToken.isPresent()) {
                UserDeviceToken token = existingDeviceToken.get();
                token.updateFcmToken(dto.getFcmToken());
                token.activate();
                log.info("디바이스 토큰 갱신 - UserNo: {}, DeviceId: {}", userNo, dto.getDeviceId());
                return token;
            }
        }

        // 새로운 토큰 등록
        UserDeviceToken newToken = UserDeviceToken.builder()
                .user(user)
                .fcmToken(dto.getFcmToken())
                .deviceType(dto.getDeviceType())
                .deviceId(dto.getDeviceId())
                .deviceModel(dto.getDeviceModel())
                .osVersion(dto.getOsVersion())
                .appVersion(dto.getAppVersion())
                .isActive(true)
                .build();

        newToken = deviceTokenRepository.save(newToken);
        log.info("새 디바이스 토큰 등록 - UserNo: {}, DeviceType: {}", userNo, dto.getDeviceType());

        return newToken;
    }

    /**
     * 디바이스 토큰 비활성화 (로그아웃 시)
     */
    public void deactivateToken(String fcmToken) {
        Optional<UserDeviceToken> token = deviceTokenRepository.findByFcmToken(fcmToken);

        if (token.isPresent()) {
            token.get().deactivate();
            log.info("디바이스 토큰 비활성화 - TokenNo: {}", token.get().getTokenNo());
        }
    }

    /**
     * 사용자의 모든 디바이스 토큰 비활성화
     */
    public void deactivateAllUserTokens(Long userNo) {
        UserMain user = userService.findByUserNo(userNo);

        List<UserDeviceToken> tokens = deviceTokenRepository.findByUserAndIsActiveTrue(user);

        tokens.forEach(UserDeviceToken::deactivate);
        log.info("사용자의 모든 토큰 비활성화 - UserNo: {}, Count: {}", userNo, tokens.size());
    }

    /**
     * 사용자의 활성화된 디바이스 목록 조회
     */
    @Transactional(readOnly = true)
    public List<UserDeviceToken> getActiveDevices(Long userNo) {
        UserMain user = userService.findByUserNo(userNo);

        return deviceTokenRepository.findByUserAndIsActiveTrue(user);
    }

    /**
     * 특정 디바이스 토큰 삭제
     */
    public void deleteToken(Long tokenNo, Long userNo) {
        UserDeviceToken token = deviceTokenRepository.findById(tokenNo)
                .orElseThrow(() -> new IllegalArgumentException("토큰을 찾을 수 없습니다."));

        if (!token.getUser().getUserNo().equals(userNo)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        deviceTokenRepository.delete(token);
        log.info("디바이스 토큰 삭제 - TokenNo: {}, UserNo: {}", tokenNo, userNo);
    }
}
