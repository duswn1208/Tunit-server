package com.tunit.domain.tutor.repository;

import com.tunit.domain.tutor.define.TutorLessonOpenType;
import com.tunit.domain.tutor.entity.TutorAvailException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface TutorAvailExceptionRepository extends JpaRepository<TutorAvailException, Long> {
    @Query("SELECT COUNT(t) > 0 FROM TutorAvailException t " +
            "WHERE t.tutorProfileNo = :tutorProfileNo " +
            "AND t.date = :date " +
            "AND t.type = :type " +
            "AND :startTime <= t.endTime " +
            "AND :endTime >= t.startTime")
    boolean exist(
            Long tutorProfileNo,
            LocalDate date,
            TutorLessonOpenType type,
            LocalTime startTime,
            LocalTime endTime
    );

    boolean existsByTutorProfileNoAndDateAndType(Long tutorProfileNo, LocalDate date, TutorLessonOpenType type);

    List<TutorAvailException> findByTutorProfileNo(Long tutorProfileNo);
}
