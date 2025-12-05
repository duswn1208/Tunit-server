package com.tunit.domain.notification.dto;

import com.tunit.domain.notification.define.DeviceType;
import lombok.Builder;
import lombok.Getter;

/**
 * 디바이스 토큰 등록 요청 DTO
 */
@Getter
@Builder
public class DeviceTokenRegisterDto {
    private String fcmToken;
    private DeviceType deviceType;
    private String deviceId;
    private String deviceModel;
    private String osVersion;
    private String appVersion;
}

