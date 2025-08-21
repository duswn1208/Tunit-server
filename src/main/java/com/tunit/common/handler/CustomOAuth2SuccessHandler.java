package com.tunit.common.handler;

import com.tunit.domain.user.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

//        boolean isOnboardingDone = userService.checkOnboarding(oAuth2User.getId());

        boolean isOnboardingDone = false;

        if (isOnboardingDone) {
            response.sendRedirect("http://localhost:5173/mypage");
        } else {
            response.sendRedirect("http://localhost:5173/onboarding");
        }
    }
}
