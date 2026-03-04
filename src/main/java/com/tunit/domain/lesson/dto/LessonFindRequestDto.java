package com.tunit.domain.lesson.dto;

import com.tunit.domain.lesson.define.ReservationStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class LessonFindRequestDto {
    private Long tutorProfileNo;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;
    private List<ReservationStatus> reservationStatuses;

    public void setScheduleInfo(Long tutorProfileNo) {
        this.tutorProfileNo = tutorProfileNo;
        this.reservationStatuses = List.of(ReservationStatus.ACTIVE, ReservationStatus.COMPLETED, ReservationStatus.REQUESTED);
    }
}
