package com.tunit.domain.lesson.repository;

import com.tunit.domain.lesson.feedback.LessonLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LessonLogRepository extends JpaRepository<LessonLog, Long> {
    Optional<LessonLog> findByLessonReservationNo(Long lessonReservationNo);
    boolean existsByLessonReservationNo(Long lessonReservationNo);
    List<LessonLog> findByTutorProfileNoOrderByCreatedAtDesc(Long tutorProfileNo);
    List<LessonLog> findByStudentNoOrderByCreatedAtDesc(Long studentNo);
    List<LessonLog> findByLessonReservationNoIn(List<Long> lessonReservationNos);
}
