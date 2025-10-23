package com.tunit.domain.student.dto;

import com.tunit.domain.lesson.define.LessonCategory;
import com.tunit.domain.lesson.define.ReservationStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class FindMyLessonsRequestDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private List<ReservationStatus> reservationStatuses;
    private LessonCategory lessonCategory;

    public void setDefaultValuesIfNull() {
        if (this.startDate == null) {
            this.startDate = LocalDate.now().withDayOfMonth(1);
        }
        if (this.endDate == null) {
            this.endDate = this.startDate.plusMonths(1).withDayOfMonth(1).minusDays(1);
        }
        if (this.reservationStatuses == null) {
            this.reservationStatuses = ReservationStatus.NOT_ENROLLED_STATUSES;
        }
    }
}
