package com.tunit.domain.student.dto;

import com.tunit.domain.lesson.define.LessonCategory;
import com.tunit.domain.lesson.define.ReservationStatus;
import com.tunit.domain.lesson.entity.LessonReservation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@Setter
public class FindMyLessonsRequestDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private LessonReservationGroup lessonFilter;
    private LessonCategory lessonCategory;

    public FindMyLessonsRequestDto(LocalDate startDate, LocalDate endDate, LessonReservationGroup lessonFilter, LessonCategory lessonCategory) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.lessonFilter = lessonFilter;
        this.lessonCategory = lessonCategory;
    }

    public void setDefaultValuesIfNull() {
        if (this.startDate == null) {
            this.startDate = LocalDate.now().withDayOfMonth(1);
        }
        if (this.endDate == null) {
            this.endDate = this.startDate.plusMonths(2).withDayOfMonth(1).minusDays(1);
        }
        if (this.lessonFilter == null) {
            this.lessonFilter = LessonReservationGroup.UPCOMING;
        }
    }

    @Getter
    @RequiredArgsConstructor
    public enum LessonReservationGroup {
        UPCOMING("예정된 레슨", List.of(ReservationStatus.TRIAL_ACTIVE, ReservationStatus.ACTIVE)),
        PAST("지난 레슨", List.of(ReservationStatus.COMPLETED, ReservationStatus.CANCELED, ReservationStatus.EXPIRED, ReservationStatus.TRIAL_COMPLETED, ReservationStatus.TRIAL_CANCELED)),
        PENDING("대기 중인 레슨", List.of(ReservationStatus.REQUESTED, ReservationStatus.TRIAL_REQUESTED));

        private final String label;
        private final List<ReservationStatus> includedStatuses;
    }
}
