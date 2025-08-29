package com.tunit.domain.lesson.repository;

import com.tunit.domain.lesson.entity.LessonReservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonRepository extends JpaRepository<LessonReservation, Long> {
    boolean existsByTutorProfileNo(Long tutorProfileNo);

    boolean existsByUserNo(Long userNo);
}

