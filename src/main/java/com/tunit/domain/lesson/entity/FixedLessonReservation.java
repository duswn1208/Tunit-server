package com.tunit.domain.lesson.entity;

import com.tunit.domain.lesson.define.LessonSubCategory;
import com.tunit.domain.lesson.define.ReservationStatus;
import com.tunit.domain.lesson.dto.FixedLessonSaveDto;
import com.tunit.domain.tutor.dto.TutorProfileResponseDto;
import com.tunit.domain.user.entity.UserMain;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.tunit.domain.lesson.define.ReservationStatus.ACTIVE;

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

    @Column(name = "student_no", nullable = false)
    private Long studentNo;

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

    private LessonSubCategory subCategory;

    @Column(name = "memo", length = 255)
    private String memo; // 엑셀에서 받은 메모 필드

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static FixedLessonReservation getFixedLessonReservation(FixedLessonSaveDto dto, Integer day, UserMain student, TutorProfileResponseDto tutorProfileInfo) {
        return FixedLessonReservation.builder()
                .tutorProfileNo(tutorProfileInfo.tutorProfileNo())
                .studentNo(student.getUserNo())
                .dayOfWeekNum(day)
                .startTime(dto.startTime())
                .endTime(dto.startTime().plusMinutes(tutorProfileInfo.durationMin()))
                .status(dto.reservationStatus())
                .startDate(dto.firstLessonDate())
                .memo(dto.memo())
                .subCategory(LessonSubCategory.fromName(dto.lesson()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

}
