package com.tunit.domain.lesson.entity;

import com.tunit.domain.lesson.define.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "fixed_lesson_reservation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FixedLessonReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fixed_lesson_reservation_no")
    private Long fixedLessonReservationNo;

    @Column(name = "tutor_profile_no", nullable = false)
    private Long tutorProfileNo;

    @Column(name = "user_no", nullable = false)
    private Long userNo;

    @Column(name = "day_of_week_num", nullable = false)
    private Integer dayOfWeekNum;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private ReservationStatus status;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "next_materialize_date")
    private LocalDate nextMaterializeDate;

    @Column(name = "field", length = 50)
    private String field; // 자유 필드(비고용)
}
