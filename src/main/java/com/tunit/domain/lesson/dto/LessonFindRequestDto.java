package com.tunit.domain.lesson.dto;

import com.tunit.domain.lesson.define.ReservationStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class LessonFindRequestDto {
    private Long tutorProfileNo;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<ReservationStatus> reservationStatuses;

    public void setScheduleInfo(Long tutorProfileNo) {
        this.tutorProfileNo = tutorProfileNo;
        this.reservationStatuses = List.of(ReservationStatus.ACTIVE, ReservationStatus.COMPLETED, ReservationStatus.TRIAL_REQUESTED, ReservationStatus.TRIAL_ACTIVE, ReservationStatus.TRIAL_COMPLETED, ReservationStatus.REQUESTED);
    }
}
