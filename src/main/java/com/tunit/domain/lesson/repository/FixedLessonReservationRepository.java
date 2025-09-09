package com.tunit.domain.lesson.repository;


import com.tunit.domain.lesson.entity.FixedLessonReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;

public interface FixedLessonReservationRepository extends JpaRepository<FixedLessonReservation, Long> {
    boolean existsByTutorProfileNoAndDayOfWeekNumAndStartTimeAfterAndEndTimeBefore(Long tutorProfileNo, Integer dayOfWeekNum, LocalTime startTime, LocalTime endTime);
}
