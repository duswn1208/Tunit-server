package com.tunit.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 비동기 처리 설정
 * 알림 전송 시 메인 스레드를 블로킹하지 않도록 합니다
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}

