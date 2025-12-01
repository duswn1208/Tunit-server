package com.tunit.domain.user.oauth2;

import com.tunit.domain.user.define.UserProvider;

import java.util.Map;

/**
 * OAuth2 제공자에 따라 적절한 OAuth2UserInfo 구현체를 생성하는 팩토리 클래스
 * 새로운 제공자 추가 시 이 클래스만 수정하면 됨
 */
public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        UserProvider provider = UserProvider.fromRegistrationId(registrationId);

        return switch (provider) {
            case NAVER -> new NaverOAuth2UserInfo(attributes);
            case KAKAO -> new KakaoOAuth2UserInfo(attributes);
            case GOOGLE -> new GoogleOAuth2UserInfo(attributes);
            case APPLE -> new AppleOAuth2UserInfo(attributes);
        };
    }
}

