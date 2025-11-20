package com.tunit.batch.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchConfig {
    // Spring Boot 3.x는 @EnableBatchProcessing 없이도 자동 설정됨
    // @EnableBatchProcessing을 사용하면 오히려 자동 설정이 비활성화됨
}
