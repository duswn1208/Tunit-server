package com.tunit.domain.tutor.repository;

import com.tunit.domain.tutor.entity.TutorAvailException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface TutorAvailExceptionRepository extends JpaRepository<TutorAvailException, Long> {

    List<TutorAvailException> findByTutorProfileNo(Long tutorProfileNo);

    boolean existsByTutorProfileNoAndDateAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(Long tutorProfileNo, LocalDate date, LocalTime startTime, LocalTime endTime);
}
