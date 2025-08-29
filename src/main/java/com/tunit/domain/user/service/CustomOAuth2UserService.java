package com.tunit.domain.user.service;

import com.tunit.common.session.dto.SessionUser;
import com.tunit.domain.tutor.service.TutorProfileService;
import com.tunit.domain.user.define.UserProvider;
import com.tunit.domain.user.entity.UserMain;
import com.tunit.domain.user.exception.UserException;
import jakarta.servlet.http.HttpSession;
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

    private final HttpSession httpSession;
    private final UserService userService;
    private final TutorProfileService tutorProfileService;

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
        UserMain userMain;
        Long tutorProfileNo = null;
        try {
            userMain = userService.getUserProviderInfo(UserMain.findFrom(UserProvider.NAVER, providerId));

            if (userMain.getUserRole().isTutor()) {
                tutorProfileNo = tutorProfileService.findByUserNo(userMain.getUserNo()).getTutorProfileNo();
            }

        } catch (UserException e) {
            userMain = userService.saveUser(UserMain.saveOAuthNaver(name, phone, providerId));
        }

        if (tutorProfileNo != null) {
            httpSession.setAttribute("LOGIN_USER", SessionUser.create(userMain, tutorProfileNo));
        } else {
            httpSession.setAttribute("LOGIN_USER", SessionUser.create(userMain));
        }
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                response,
                "id"
        );
    }
}
