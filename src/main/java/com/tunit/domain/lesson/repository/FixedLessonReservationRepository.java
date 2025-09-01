package com.tunit.domain.lesson.repository;


import com.tunit.domain.lesson.entity.FixedLessonReservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FixedLessonReservationRepository extends JpaRepository<FixedLessonReservation, Long> {
}
