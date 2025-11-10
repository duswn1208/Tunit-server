package com.tunit.domain.tutor.repository;

import com.tunit.domain.tutor.entity.TutorAvailableTime;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface TutorAvailableTimeRepository extends JpaRepository<TutorAvailableTime, Long> {
    boolean existsByTutorProfileNoAndDayOfWeekNum(Long tutorProfileNo, Integer dayOfWeekNum);

    List<TutorAvailableTime> findAllByTutorProfileNoOrderByDayOfWeekNumAscStartTimeAsc(Long tutorProfileNo);

    boolean existsByTutorProfileNoAndDayOfWeekNumAndStartTimeGreaterThanEqualAndEndTimeLessThanEqual(Long tutorProfileNo, Integer dayOfWeekNum, LocalTime startTime, LocalTime endTime);

    boolean existsByTutorProfileNoAndTutorAvailableTimeNoIn(@NonNull Long tutorProfileNo, List<Long> tutorAvailableTimeNos);

    void deleteAllByTutorProfileNoAndTutorAvailableTimeNoIn(@NonNull Long tutorProfileNo, List<Long> tutorAvailableTimeNos);

    @Query("SELECT COUNT(t) > 0 FROM TutorAvailableTime t " +
            "WHERE t.tutorProfileNo = :tutorProfileNo " +
            "AND t.dayOfWeekNum = :dayOfWeekNum " +
            "AND t.tutorAvailableTimeNo != :tutorAvailableTimeNo " +
            "AND t.startTime < :endTime AND t.endTime > :startTime")
    boolean existsByTutorProfileNoAndDayOfWeekNumAndTimeOverlapping(
            Long tutorProfileNo,
            Integer dayOfWeekNum,
            LocalTime startTime,
            LocalTime endTime,
            Long tutorAvailableTimeNo
    );

    List<TutorAvailableTime> findAllByTutorProfileNo(Long tutorProfileNo);

    @Query("SELECT COUNT(t) > 0 FROM TutorAvailableTime t " +
            "WHERE t.tutorProfileNo = :tutorProfileNo " +
            "AND t.dayOfWeekNum = :dayOfWeekNum " +
            "AND :requestTime >= t.startTime " +
            "AND :requestTime < t.endTime")
    boolean existsByTutorProfileNoAndDayOfWeekNumAndRequestTimeBetweenStartTimeAndEndTime(
            Long tutorProfileNo,
            Integer dayOfWeekNum,
            LocalTime requestTime
    );
}
