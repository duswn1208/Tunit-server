package com.tunit.domain.tutor.repository;

import com.tunit.domain.tutor.define.TutorLessonOpenType;
import com.tunit.domain.tutor.entity.TutorAvailException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface TutorAvailExceptionRepository extends JpaRepository<TutorAvailException, Long> {
    boolean existsByTutorProfileNoAndDateAndTypeAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
            Long tutorProfileNo,
            LocalDate date,
            TutorLessonOpenType type,
            LocalTime startTime,
            LocalTime endTime
    );

    List<TutorAvailException> findByTutorProfileNo(Long tutorProfileNo);

    boolean existsByTutorProfileNoAndDateAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(Long tutorProfileNo, LocalDate date, LocalTime startTime, LocalTime endTime);
}
