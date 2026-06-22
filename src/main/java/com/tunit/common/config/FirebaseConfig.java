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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

/**
 * Firebase Cloud Messaging 설정
 */
@Configuration
public class FirebaseConfig {

    // 서비스 계정 키(JSON)를 base64 로 인코딩한 값. 배포 환경에서 환경변수로 주입한다.
    @Value("${firebase.config.base64:}")
    private String firebaseConfigBase64;

    // 서비스 계정 키 파일 경로. Render Secret File(/etc/secrets/..) 같은 외부 파일이면 파일시스템에서,
    // 아니면 classpath(로컬 개발)에서 읽는다.
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
        // 1순위: base64 env (선택)
        if (StringUtils.hasText(firebaseConfigBase64)) {
            // 환경변수에 줄바꿈/공백이 섞여도 디코딩되도록 모든 공백 제거 후 MIME 디코더 사용
            String cleaned = firebaseConfigBase64.replaceAll("\\s", "");
            byte[] decoded = Base64.getMimeDecoder().decode(cleaned);
            return new ByteArrayInputStream(decoded);
        }
        // 2순위: 파일시스템 경로 (Render Secret File 등 외부 파일)
        File file = new File(firebaseConfigPath);
        if (file.exists()) {
            return new FileInputStream(file);
        }
        // 3순위: classpath (로컬 개발)
        return new ClassPathResource(firebaseConfigPath).getInputStream();
    }
}

