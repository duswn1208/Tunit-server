package com.tunit.domain.lesson.repository;


import com.tunit.domain.lesson.entity.FixedLessonReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;

public interface FixedLessonReservationRepository extends JpaRepository<FixedLessonReservation, Long> {
    boolean existsByTutorProfileNoAndStudentNoAndDayOfWeekNum(Long tutorProfileNo, Long userNo, Integer dayOfWeekNum);
}
