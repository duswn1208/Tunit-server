package com.tunit.batch.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class LessonBatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job expireLessonsJob;
    private final Job completeLessonsJob;

    /**
     * 만료된 레슨 처리 스케줄러
     * 매 30분마다 실행 (00분, 30분)
     */
    @Scheduled(cron = "0 0,30 * * * *")
    public void runExpireLessonsJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            log.info("=== 만료 레슨 배치 작업 시작 ===");
            jobLauncher.run(expireLessonsJob, jobParameters);
            log.info("=== 만료 레슨 배치 작업 완료 ===");
        } catch (Exception e) {
            log.error("만료 레슨 배치 작업 실행 중 오류 발생", e);
        }
    }

    /**
     * 완료된 레슨 처리 스케줄러
     * 매시간 10분에 실행
     */
    @Scheduled(cron = "0 10 * * * *")
    public void runCompleteLessonsJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            log.info("=== 레슨 완료 배치 작업 시작 ===");
            jobLauncher.run(completeLessonsJob, jobParameters);
            log.info("=== 레슨 완료 배치 작업 완료 ===");
        } catch (Exception e) {
            log.error("레슨 완료 배치 작업 실행 중 오류 발생", e);
        }
    }
}

