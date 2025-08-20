package com.tunit.domain.tutor.repository;

import com.tunit.domain.tutor.entity.TutorAvailableTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface TutorAvailableTimeRepository extends JpaRepository<TutorAvailableTime, Long> {
    List<TutorAvailableTime> findAllByTutorProfileNo(Long tutorProfileNo);

    boolean existsByTutorProfileNoAndDayOfWeekAndStartTimeAndEndTime(Long tutorProfileNo, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime);
}
