package com.tunit.common.session.service;

import com.tunit.common.session.annotation.LoginUser;
import com.tunit.common.session.dto.SessionUser;
import com.tunit.common.session.exception.SessionNotFoundException;
import com.tunit.domain.user.entity.UserMain;
import com.tunit.domain.user.exception.UserException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final HttpSession session;

    public LoginUserArgumentResolver(HttpSession session) {
        this.session = session;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (!parameter.hasParameterAnnotation(LoginUser.class)) {
            return false;
        }
        // SessionUser 타입 또는 Long, Integer, String 등 기본 타입 지원
        Class<?> paramType = parameter.getParameterType();
        return paramType.equals(SessionUser.class)
                || paramType.equals(Long.class)
                || paramType.equals(long.class)
                || paramType.equals(Integer.class)
                || paramType.equals(int.class)
                || paramType.equals(String.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        assert request != null;
        SessionUser sessionUserObj = (SessionUser) request.getSession().getAttribute("LOGIN_USER");
        if (sessionUserObj == null) {
            throw new SessionNotFoundException();
        }
        Class<?> paramType = parameter.getParameterType();
        if (paramType.equals(SessionUser.class)) {
            return sessionUserObj;
        }
        // 필드명으로 특정 값 반환
        LoginUser loginUser = parameter.getParameterAnnotation(LoginUser.class);
        String field = loginUser.field();
        if (field == null || field.isEmpty()) {
            return null;
        }
        switch (field) {
            case "userNo":
                return sessionUserObj.userNo();
            case "tutorProfileNo":
                return sessionUserObj.tutorProfileNo();
            // 필요시 다른 필드 추가
            default:
                return null;
        }
    }
}
