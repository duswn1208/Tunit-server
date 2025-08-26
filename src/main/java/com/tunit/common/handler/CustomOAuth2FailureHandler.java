package com.tunit.common.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class CustomOAuth2FailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        // 1) 에러 코드/메시지 추출
        String errorCode = "oauth2_auth_failed";
        String errorMessage = exception.getMessage();

        if (exception instanceof OAuth2AuthenticationException oae) {
            OAuth2Error err = oae.getError();
            if (err != null && err.getErrorCode() != null) {
                errorCode = err.getErrorCode();
            }
            // 네이버가 넘겨준 error, error_description 파라미터도 한 번 더 확인
            String providerError = request.getParameter("error");
            String providerDesc  = request.getParameter("error_description");
            if (providerError != null && !providerError.isBlank()) errorCode = providerError;
            if (providerDesc != null && !providerDesc.isBlank())  errorMessage = providerDesc;
        }

        log.warn("[OAuth2 Failure] code={}, msg={}", errorCode, errorMessage, exception);

        // 2) AJAX 요청이면 JSON 401로 응답
        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
        if (isAjax) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            String body = """
                {"error":"%s","message":"%s"}
                """.formatted(jsonEscape(errorCode), jsonEscape(errorMessage));
            response.getWriter().write(body);
            return;
        }

        // 3) 페이지 네비게이션이면 로그인 페이지로 리다이렉트
        String redirectUrl = UriComponentsBuilder.fromPath("/login")
                .queryParam("error", errorCode)
                .queryParam("message", urlEncodeLimit(errorMessage, 300)) // 너무 길면 잘라서 인코딩
                .build()
                .toUriString();

        response.sendRedirect(redirectUrl);
    }

    private static String urlEncodeLimit(String s, int maxLen) {
        if (s == null) return "";
        String cut = s.length() > maxLen ? s.substring(0, maxLen) : s;
        return URLEncoder.encode(cut, StandardCharsets.UTF_8);
    }

    private static String jsonEscape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
