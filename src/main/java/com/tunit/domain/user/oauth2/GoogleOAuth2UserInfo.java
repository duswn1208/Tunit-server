package com.tunit.domain.user.oauth2;

import java.util.Map;

/**
 * 구글 OAuth2 사용자 정보 추출
 * 구글은 최상위 레벨에 바로 사용자 정보가 있음 (가장 단순)
 */
public class GoogleOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getPhone() {
        // 구글은 기본적으로 전화번호를 제공하지 않음
        return (String) attributes.get("phone_number");
    }

    @Override
    public String getProfileImageUrl() {
        return (String) attributes.get("picture");
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}

