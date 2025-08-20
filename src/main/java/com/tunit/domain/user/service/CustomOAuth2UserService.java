package com.tunit.domain.user.service;

import com.tunit.domain.user.define.UserProvider;
import com.tunit.domain.user.entity.User;
import com.tunit.domain.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("Loading OAuth2User for client: {}", userRequest.getClientRegistration().getRegistrationId());
        OAuth2User delegate = new DefaultOAuth2UserService().loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId(); // "naver"
        Map<String, Object> response = (Map<String, Object>) delegate.getAttributes().get("response");

        String providerId = (String) response.get("id");
        String name = (String) response.get("name");
        String phone = (String) response.get("mobile");

        log.info("OAuth2User loaded: provider={}, providerId={}, name={}", provider, providerId, name);
        try {
            userService.getUserProviderInfo(User.findFrom(UserProvider.NAVER, providerId));
        } catch (UserException e) {
            userService.saveUser(User.saveOAuthNaver(name, phone, providerId));
        }
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                response,
                "id"
        );
    }
}
