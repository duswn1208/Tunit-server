package com.tunit.domain.notification.repository;

import com.tunit.domain.notification.entity.UserDeviceToken;
import com.tunit.domain.notification.define.DeviceType;
import com.tunit.domain.user.entity.UserMain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDeviceTokenRepository extends JpaRepository<UserDeviceToken, Long> {

    /**
     * 사용자의 활성화된 모든 디바이스 토큰 조회
     */
    List<UserDeviceToken> findByUserAndIsActiveTrue(UserMain user);

    /**
     * 사용자의 특정 디바이스 타입의 활성화된 토큰 조회
     */
    List<UserDeviceToken> findByUserAndDeviceTypeAndIsActiveTrue(UserMain user, DeviceType deviceType);

    /**
     * FCM 토큰으로 조회
     */
    Optional<UserDeviceToken> findByFcmToken(String fcmToken);

    /**
     * 사용자와 디바이스 ID로 조회
     */
    Optional<UserDeviceToken> findByUserAndDeviceId(UserMain user, String deviceId);

    /**
     * 사용자의 모든 토큰 비활성화
     */
    void deleteByUser(UserMain user);
}

