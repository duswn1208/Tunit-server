package com.tunit.domain.user.oauth2;

import java.util.Map;

/**
 * 카카오 OAuth2 사용자 정보 추출
 * 카카오는 id, kakao_account, properties 등으로 정보가 분산되어 있음
 */
public class KakaoOAuth2UserInfo implements OAuth2UserInfo {
    
    private final Map<String, Object> attributes;
    private final Map<String, Object> kakaoAccount;
    private final Map<String, Object> properties;
    
    @SuppressWarnings("unchecked")
    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        this.properties = (Map<String, Object>) attributes.get("properties");
    }

    @Override
    public String getProviderId() {
        Object id = attributes.get("id");
        return id != null ? String.valueOf(id) : null;
    }

    @Override
    public String getName() {
        if (kakaoAccount != null) {
            return (String) kakaoAccount.get("name");
        }
        // name이 없으면 nickname 사용
        if (properties != null) {
            return (String) properties.get("nickname");
        }
        return null;
    }

    @Override
    public String getEmail() {
        if (kakaoAccount != null) {
            return (String) kakaoAccount.get("email");
        }
        return null;
    }

    @Override
    public String getPhone() {
        if (kakaoAccount != null) {
            String phoneNumber = (String) kakaoAccount.get("phone_number");
            if (phoneNumber != null) {
                // 카카오는 "+82 10-1234-5678" 형태로 반환
                return phoneNumber.replace("+82 ", "0").replace("-", "").trim();
            }
        }
        return null;
    }

    @Override
    public String getProfileImageUrl() {
        if (properties != null) {
            return (String) properties.get("profile_image");
        }
        if (kakaoAccount != null) {
            @SuppressWarnings("unchecked")
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            if (profile != null) {
                return (String) profile.get("profile_image_url");
            }
        }
        return null;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
