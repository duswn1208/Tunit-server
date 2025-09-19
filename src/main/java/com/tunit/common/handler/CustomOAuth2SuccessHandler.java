package com.tunit.common.handler;

import com.tunit.domain.tutor.service.TutorProfileService;
import com.tunit.domain.user.entity.UserMain;
import com.tunit.domain.student.service.StudentService;
import com.tunit.domain.user.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final StudentService studentService;
    @Value("${service-url.web}")
    private String redirectUrl;
    private final UserService userService;
    private final TutorProfileService tutorProfileService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        final String HOME_PATH = redirectUrl + "/";
        final String ONBOARDING_PATH = redirectUrl + "/onboarding";

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String providerId = oAuth2User.getAttribute("id");

        UserMain user = userService.findByProviderId(providerId);
        if (user == null) {
            response.sendRedirect(ONBOARDING_PATH);
            return;
        }

        if (user.getUserRole() == null) {
            response.sendRedirect(ONBOARDING_PATH);
            return;
        }

        switch (user.getUserRole()) {
            case TUTOR -> {
                boolean hasTutorProfile = tutorProfileService.findByUserNo(user.getUserNo()) != null;
                if (!hasTutorProfile) {
                    response.sendRedirect(ONBOARDING_PATH);
                } else {
                    response.sendRedirect(HOME_PATH);
                }
            }
            case STUDENT -> {
                boolean needOnboardingStudent = studentService.needOnboardingStudent(user.getUserNo());
                if (needOnboardingStudent) {
                    response.sendRedirect(ONBOARDING_PATH);
                } else {
                    response.sendRedirect(HOME_PATH);
                }
            }
            default -> response.sendRedirect(HOME_PATH);
        }
    }
}
