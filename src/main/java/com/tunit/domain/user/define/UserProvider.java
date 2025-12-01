package com.tunit.domain.user.define;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public enum UserProvider {
    NAVER("naver"),
    KAKAO("kakao"),
    GOOGLE("google"),
    APPLE("apple");

    private String registrationId;

    UserProvider(String registrationId) {
        this.registrationId = registrationId;
    }

    public static UserProvider fromRegistrationId(String registrationId) {
        for (UserProvider provider : values()) {
            if (provider.getRegistrationId().equalsIgnoreCase(registrationId)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("Unknown provider: " + registrationId);
    }
}
