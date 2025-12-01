package com.tunit.domain.user.oauth2;

import java.util.Map;

/**
 * OAuth2 제공자로부터 받은 사용자 정보를 추상화하는 인터페이스
 * 각 제공자(네이버, 카카오, 구글, 애플)마다 다른 응답 구조를 통일된 방식으로 처리
 */
public interface OAuth2UserInfo {

    /**
     * 제공자별 고유 ID (providerId)
     */
    String getProviderId();

    /**
     * 사용자 이름
     */
    String getName();

    /**
     * 이메일 주소
     */
    String getEmail();

    /**
     * 전화번호
     */
    String getPhone();

    /**
     * 프로필 이미지 URL
     */
    String getProfileImageUrl();

    /**
     * 원본 attributes (필요시 접근)
     */
    Map<String, Object> getAttributes();
}

