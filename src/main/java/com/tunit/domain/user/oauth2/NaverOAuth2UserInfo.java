package com.tunit.domain.user.oauth2;

import java.util.Map;

/**
 * 네이버 OAuth2 사용자 정보 추출
 * 네이버는 "response" 객체 안에 사용자 정보가 들어있음
 */
public class NaverOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;
    private final Map<String, Object> response;

    @SuppressWarnings("unchecked")
    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        // 네이버는 response 객체 안에 실제 사용자 정보가 있음
        this.response = (Map<String, Object>) attributes.get("response");
    }

    @Override
    public String getProviderId() {
        return (String) response.get("id");
    }

    @Override
    public String getName() {
        return (String) response.get("name");
    }

    @Override
    public String getEmail() {
        return (String) response.get("email");
    }

    @Override
    public String getPhone() {
        String mobile = (String) response.get("mobile");
        if (mobile != null) {
            // 네이버는 "010-1234-5678" 형태로 반환하므로 - 제거
            return mobile.replace("-", "");
        }
        return null;
    }

    @Override
    public String getProfileImageUrl() {
        return (String) response.get("profile_image");
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}

