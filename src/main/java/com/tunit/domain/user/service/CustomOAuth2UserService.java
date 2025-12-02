package com.tunit.domain.user.service;

import com.tunit.common.session.dto.SessionUser;
import com.tunit.domain.tutor.entity.TutorProfile;
import com.tunit.domain.tutor.service.TutorProfileService;
import com.tunit.domain.user.define.UserProvider;
import com.tunit.domain.user.entity.UserMain;
import com.tunit.domain.user.exception.UserException;
import com.tunit.domain.user.oauth2.OAuth2UserInfo;
import com.tunit.domain.user.oauth2.OAuth2UserInfoFactory;
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

/**
 * 확장 가능한 OAuth2 사용자 서비스
 * 네이버, 카카오, 구글, 애플 모두 동일한 로직으로 처리
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final HttpSession httpSession;
    private final UserService userService;
    private final TutorProfileService tutorProfileService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.info("OAuth2 로그인 시도: provider={}", registrationId);

        // 1. OAuth2 제공자로부터 사용자 정보 가져오기
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        // 2. 제공자에 따라 적절한 UserInfo 객체 생성 (팩토리 패턴)
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                registrationId,
                oAuth2User.getAttributes()
        );

        UserProvider provider = UserProvider.fromRegistrationId(registrationId);

        log.info("OAuth2 사용자 정보 추출 완료: provider={}, providerId={}, name={}, email={}",
                provider, userInfo.getProviderId(), userInfo.getName(), userInfo.getEmail());

        // 3. 사용자 조회 또는 생성
        UserMain userMain = processOAuth2User(provider, userInfo);

        // 4. 세션에 사용자 정보 저장
        saveUserToSession(userMain);

        // 5. Spring Security OAuth2User 반환
        return createOAuth2User(userInfo, provider);
    }

    /**
     * OAuth2 사용자 처리 (조회 또는 생성)
     */
    private UserMain processOAuth2User(UserProvider provider, OAuth2UserInfo userInfo) {
        try {
            // 기존 사용자 조회
            return userService.getUserProviderInfo(
                    UserMain.findFrom(provider, userInfo.getProviderId())
            );
        } catch (UserException e) {
            // 신규 사용자 생성
            log.info("신규 OAuth2 사용자 생성: provider={}, providerId={}", provider, userInfo.getProviderId());
            return userService.saveUser(UserMain.createOAuthUser(provider, userInfo));
        }
    }

    /**
     * 세션에 사용자 정보 저장
     */
    private void saveUserToSession(UserMain userMain) {
        Long tutorProfileNo = null;

        // 튜터인 경우 튜터 프로필 번호 조회
        if (userMain.getUserRole() != null && userMain.getUserRole().isTutor()) {
            TutorProfile tutorProfile = tutorProfileService.findByUserNo(userMain.getUserNo());
            if (tutorProfile != null) {
                tutorProfileNo = tutorProfile.getTutorProfileNo();
            }
        }

        // 세션에 저장
        if (tutorProfileNo != null) {
            httpSession.setAttribute("LOGIN_USER", SessionUser.create(userMain, tutorProfileNo));
        } else {
            httpSession.setAttribute("LOGIN_USER", SessionUser.create(userMain));
        }

        log.info("세션 저장 완료: userNo={}, role={}", userMain.getUserNo(), userMain.getUserRole());
    }

    /**
     * Spring Security OAuth2User 객체 생성
     * 제공자별로 nameAttributeKey가 다르므로 적절히 처리
     */
    private DefaultOAuth2User createOAuth2User(OAuth2UserInfo userInfo, UserProvider provider) {
        String nameAttributeKey = provider.getNameAttributeKey();

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                getAttributes(provider, userInfo),
                nameAttributeKey
        );
    }

    private Map<String, Object> getAttributes(UserProvider provider, OAuth2UserInfo userInfo) {
        return switch (provider) {
            case NAVER -> (Map<String, Object>)userInfo.getAttributes().get("response");  // response.id
            default -> userInfo.getAttributes();
        };
    }

}
