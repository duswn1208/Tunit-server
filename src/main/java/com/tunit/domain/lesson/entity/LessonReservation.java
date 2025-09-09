package com.tunit.domain.lesson.entity;

import com.tunit.domain.lesson.define.LessonSubCategory;
import com.tunit.domain.lesson.define.ReservationSource;
import com.tunit.domain.lesson.define.ReservationStatus;
import com.tunit.domain.lesson.dto.LessonReserveSaveDto;
import com.tunit.domain.tutor.dto.TutorProfileResponseDto;
import com.tunit.domain.user.entity.UserMain;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
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

    @Column(name = "student_no", nullable = false)
    private Long studentNo;  // 학생 user_no

    private LessonSubCategory lessonCategory;

    @Column(name = "fixed_lesson_reservation_no", nullable = false)
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
    public LessonReservation(Long lessonReservationNo, Long tutorProfileNo, Long studentNo, LessonSubCategory lessonCategory, Long fixedLessonReservationNo, LocalDate date,
                             LocalTime startTime, LocalTime endTime, Integer dayOfWeekNum, ReservationStatus status, String idempotencyKey,
                             ReservationSource source, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.lessonReservationNo = lessonReservationNo;
        this.tutorProfileNo = tutorProfileNo;
        this.studentNo = studentNo;
        this.lessonCategory = lessonCategory;
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

    public static LessonReservation fromFixedLessonExcelUpload(FixedLessonReservation fixedLessonReservation) {
        return LessonReservation.of()
                .tutorProfileNo(fixedLessonReservation.getTutorProfileNo())
                .studentNo(fixedLessonReservation.getStudentNo())
                .fixedLessonReservationNo(fixedLessonReservation.getFixedLessonReservationNo())
                .lessonCategory(fixedLessonReservation.getSubCategory())
                .date(fixedLessonReservation.getStartDate())
                .startTime(fixedLessonReservation.getStartTime())
                .endTime(fixedLessonReservation.getEndTime())
                .dayOfWeekNum(fixedLessonReservation.getDayOfWeekNum())
                .status(fixedLessonReservation.getStatus())
                .source(ReservationSource.IMPORT)
                .build();
    }

    public static LessonReservation fromFixedLessonFromWeb(FixedLessonReservation fixedLessonReservation) {
        return LessonReservation.of()
                .tutorProfileNo(fixedLessonReservation.getTutorProfileNo())
                .studentNo(fixedLessonReservation.getStudentNo())
                .fixedLessonReservationNo(fixedLessonReservation.getFixedLessonReservationNo())
                .lessonCategory(fixedLessonReservation.getSubCategory())
                .date(fixedLessonReservation.getStartDate())
                .startTime(fixedLessonReservation.getStartTime())
                .endTime(fixedLessonReservation.getEndTime())
                .dayOfWeekNum(fixedLessonReservation.getDayOfWeekNum())
                .status(fixedLessonReservation.getStatus())
                .source(ReservationSource.APP)
                .build();
    }

    public static LessonReservation fromLessonSaveDto(TutorProfileResponseDto tutorProfileInfo, UserMain student, LessonReserveSaveDto dto) {
        LocalDateTime startDateTime = LocalDateTime.of(dto.lessonDate(), dto.startTime());
        DayOfWeek day = DayOfWeek.of(startDateTime.getDayOfWeek().getValue());
        return LessonReservation.of()
                .tutorProfileNo(tutorProfileInfo.tutorProfileNo())
                .studentNo(student.getUserNo())
                .lessonCategory(LessonSubCategory.fromLabel(dto.lesson()))
                .dayOfWeekNum(day.getValue())
                .date(dto.lessonDate())
                .startTime(dto.startTime())
                .endTime(dto.startTime().plusMinutes(tutorProfileInfo.durationMin()))
                .status(dto.reservationStatus())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }


    public void changeStatus(ReservationStatus beforeStatus, ReservationStatus afterStatus) {
        if (!this.status.equals(beforeStatus)) {
            throw new IllegalStateException("현재 상태가 " + beforeStatus + "가 아닙니다. 현재 상태: " + this.status);
        }
        if (!beforeStatus.getAllowedNextStatuses().contains(afterStatus)) {
            throw new IllegalStateException("상태를 " + beforeStatus + "에서 " + afterStatus + "로 변경할 수 없습니다.");
        }
        this.status = afterStatus;
    }
}
