package com.tunit.batch.job;

import com.tunit.domain.lesson.define.ReservationStatus;
import com.tunit.domain.lesson.entity.LessonReservation;
import com.tunit.domain.lesson.repository.LessonReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class LessonStatusBatchJob {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final LessonReservationRepository lessonReservationRepository;

    private static final int CHUNK_SIZE = 100;

    /**
     * 만료된 레슨 자동 처리 Job
     * REQUESTED 상태의 레슨 중 현재 시간이 지난 레슨을 EXPIRED로 변경
     */
    @Bean
    public Job expireLessonsJob() {
        return new JobBuilder("expireLessonsJob", jobRepository)
                .start(expireLessonsStep())
                .build();
    }

    @Bean
    public Step expireLessonsStep() {
        return new StepBuilder("expireLessonsStep", jobRepository)
                .<LessonReservation, LessonReservation>chunk(CHUNK_SIZE, transactionManager)
                .reader(expireLessonsReader())
                .processor(expireLessonsProcessor())
                .writer(expireLessonsWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<LessonReservation> expireLessonsReader() {
        return new RepositoryItemReaderBuilder<LessonReservation>()
                .name("expireLessonsReader")
                .repository(lessonReservationRepository)
                .methodName("findAll")
                .pageSize(CHUNK_SIZE)
                .sorts(Map.of("lessonReservationNo", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<LessonReservation, LessonReservation> expireLessonsProcessor() {
        return lesson -> {
            // REQUESTED 상태이고 레슨 날짜/시간이 지난 경우만 처리
            if (lesson.getStatus() == ReservationStatus.REQUESTED) {
                LocalDateTime lessonDateTime = LocalDateTime.of(lesson.getDate(), lesson.getStartTime());
                LocalDateTime now = LocalDateTime.now();

                if (lessonDateTime.isBefore(now)) {
                    log.info("만료 처리: lessonReservationNo={}, date={}, startTime={}",
                            lesson.getLessonReservationNo(), lesson.getDate(), lesson.getStartTime());
                    lesson.updateStatus(ReservationStatus.EXPIRED);
                    return lesson;
                }
            }
            return null; // 처리할 필요 없는 레슨은 null 반환
        };
    }

    @Bean
    public RepositoryItemWriter<LessonReservation> expireLessonsWriter() {
        return new RepositoryItemWriterBuilder<LessonReservation>()
                .repository(lessonReservationRepository)
                .methodName("save")
                .build();
    }

    /**
     * 완료된 레슨 자동 처리 Job
     * ACTIVE 상태의 레슨 중 종료 시간이 지난 레슨을 COMPLETED로 변경
     */
    @Bean
    public Job completeLessonsJob() {
        return new JobBuilder("completeLessonsJob", jobRepository)
                .start(completeLessonsStep())
                .build();
    }

    @Bean
    public Step completeLessonsStep() {
        return new StepBuilder("completeLessonsStep", jobRepository)
                .<LessonReservation, LessonReservation>chunk(CHUNK_SIZE, transactionManager)
                .reader(completeLessonsReader())
                .processor(completeLessonsProcessor())
                .writer(completeLessonsWriter())
                .build();
    }

    @Bean
    public RepositoryItemReader<LessonReservation> completeLessonsReader() {
        return new RepositoryItemReaderBuilder<LessonReservation>()
                .name("completeLessonsReader")
                .repository(lessonReservationRepository)
                .methodName("findAll")
                .pageSize(CHUNK_SIZE)
                .sorts(Map.of("lessonReservationNo", Sort.Direction.ASC))
                .build();
    }

    @Bean
    public ItemProcessor<LessonReservation, LessonReservation> completeLessonsProcessor() {
        return lesson -> {
            log.info("완료 처리: lessonReservationNo={}, date={}, endTime={}",
                    lesson.getLessonReservationNo(), lesson.getDate(), lesson.getEndTime());
            lesson.updateStatus(ReservationStatus.COMPLETED);
            return lesson;
        };
    }

    @Bean
    public RepositoryItemWriter<LessonReservation> completeLessonsWriter() {
        return new RepositoryItemWriterBuilder<LessonReservation>()
                .repository(lessonReservationRepository)
                .methodName("save")
                .build();
    }
}

