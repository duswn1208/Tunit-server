package com.tunit.common.config;

import com.tunit.common.session.service.LoginUserArgumentResolver;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final HttpSession httpSession;

    public WebConfig(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginUserArgumentResolver(httpSession));
    }
}
