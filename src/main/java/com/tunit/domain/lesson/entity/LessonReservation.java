package com.tunit.domain.lesson.entity;

import com.tunit.domain.lesson.define.ReservationSource;
import com.tunit.domain.lesson.define.ReservationStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "lesson_reservation")
@Getter
@NoArgsConstructor
public class LessonReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lesson_reservation_no")
    private Long lessonReservationNo;

    @Column(name = "tutor_profile_no", nullable = false)
    private Long tutorProfileNo;

    @Column(name = "user_no", nullable = false)
    private Long userNo;  // 학생 user_no

    @Column(name = "fixed_lesson_reservation_no")
    private Long fixedLessonReservationNo;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "day_of_week_num")
    private Integer dayOfWeekNum;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private ReservationStatus status;

    @Column(name = "idempotency_key", length = 50, unique = true)
    private String idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", length = 10, nullable = false)
    private ReservationSource source;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder(builderMethodName = "of")
    public LessonReservation(Long lessonReservationNo, Long tutorProfileNo, Long userNo, Long fixedLessonReservationNo, LocalDate date,
                             LocalTime startTime, LocalTime endTime, Integer dayOfWeekNum, ReservationStatus status, String idempotencyKey,
                             ReservationSource source, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.lessonReservationNo = lessonReservationNo;
        this.tutorProfileNo = tutorProfileNo;
        this.userNo = userNo;
        this.fixedLessonReservationNo = fixedLessonReservationNo;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.dayOfWeekNum = dayOfWeekNum;
        this.status = status;
        this.idempotencyKey = idempotencyKey;
        this.source = source;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static LessonReservation excelUpload(Long tutorProfileNo, Long userNo, LocalDate date, LocalTime startTime, LocalTime endTime, ReservationStatus status) {
        return LessonReservation.of()
                .tutorProfileNo(tutorProfileNo)
                .userNo(userNo)
                .date(date)
                .startTime(startTime)
                .endTime(endTime)
                .dayOfWeekNum(date.getDayOfWeek().getValue())
                .status(status)
                .source(ReservationSource.IMPORT)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
