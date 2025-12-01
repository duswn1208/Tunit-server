package com.tunit.domain.user.oauth2;

import java.util.Map;

/**
 * 애플 OAuth2 사용자 정보 추출
 * 애플은 최초 로그인 시에만 전체 정보를 제공하고, 이후에는 sub만 제공
 */
public class AppleOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    public AppleOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getName() {
        // 애플은 최초 로그인 시에만 name을 제공
        Object name = attributes.get("name");
        if (name instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> nameMap = (Map<String, Object>) name;
            String firstName = (String) nameMap.get("firstName");
            String lastName = (String) nameMap.get("lastName");
            if (firstName != null && lastName != null) {
                return lastName + firstName; // 한국식 이름 (성+이름)
            }
        }
        return null;
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getPhone() {
        // 애플은 전화번호를 제공하지 않음
        return null;
    }

    @Override
    public String getProfileImageUrl() {
        // 애플은 프로필 이미지를 제공하지 않음
        return null;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}

