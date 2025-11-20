package com.tunit.batch.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/batch")
@RequiredArgsConstructor
public class BatchController {

    private final JobLauncher jobLauncher;
    private final Job expireLessonsJob;
    private final Job completeLessonsJob;

    /**
     * 만료 레슨 배치 수동 실행
     */
    @GetMapping("/expire-lessons")
    public ResponseEntity<Map<String, String>> runExpireLessonsJob() {
        Map<String, String> response = new HashMap<>();
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            log.info("=== [수동 실행] 만료 레슨 배치 작업 시작 ===");
            jobLauncher.run(expireLessonsJob, jobParameters);
            log.info("=== [수동 실행] 만료 레슨 배치 작업 완료 ===");

            response.put("status", "success");
            response.put("message", "만료 레슨 배치 작업이 성공적으로 완료되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("만료 레슨 배치 작업 실행 중 오류 발생", e);
            response.put("status", "error");
            response.put("message", "배치 작업 실행 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 완료 레슨 배치 수동 실행
     */
    @GetMapping("/complete-lessons")
    public ResponseEntity<Map<String, String>> runCompleteLessonsJob() {
        Map<String, String> response = new HashMap<>();
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            log.info("=== [수동 실행] 레슨 완료 배치 작업 시작 ===");
            jobLauncher.run(completeLessonsJob, jobParameters);
            log.info("=== [수동 실행] 레슨 완료 배치 작업 완료 ===");

            response.put("status", "success");
            response.put("message", "레슨 완료 배치 작업이 성공적으로 완료되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("레슨 완료 배치 작업 실행 중 오류 발생", e);
            response.put("status", "error");
            response.put("message", "배치 작업 실행 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * 모든 배치 작업 수동 실행
     */
    @PostMapping("/run-all")
    public ResponseEntity<Map<String, String>> runAllBatchJobs() {
        Map<String, String> response = new HashMap<>();
        try {
            // 만료 레슨 처리
            JobParameters expireParams = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(expireLessonsJob, expireParams);

            // 완료 레슨 처리
            JobParameters completeParams = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis() + 1)
                    .toJobParameters();
            jobLauncher.run(completeLessonsJob, completeParams);

            response.put("status", "success");
            response.put("message", "모든 배치 작업이 성공적으로 완료되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("배치 작업 실행 중 오류 발생", e);
            response.put("status", "error");
            response.put("message", "배치 작업 실행 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}

