package com.tunit.common.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Firebase Cloud Messaging 설정
 */
@Configuration
public class FirebaseConfig {

    // 서비스 계정 키(JSON)를 base64 로 인코딩한 값. 배포 환경에서 환경변수로 주입한다.
    @Value("${firebase.config.base64:}")
    private String firebaseConfigBase64;

    // 로컬 개발용 fallback: classpath 에 위치한 서비스 계정 키 파일 경로
    @Value("${firebase.config.path:firebase-service-account.json}")
    private String firebaseConfigPath;

    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(resolveCredentialsStream()))
                        .build();

                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            throw new RuntimeException("Firebase 초기화 실패: " + e.getMessage(), e);
        }
    }

    private InputStream resolveCredentialsStream() throws IOException {
        if (StringUtils.hasText(firebaseConfigBase64)) {
            byte[] decoded = Base64.getDecoder().decode(firebaseConfigBase64.trim());
            return new ByteArrayInputStream(decoded);
        }
        return new ClassPathResource(firebaseConfigPath).getInputStream();
    }
}

