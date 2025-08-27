package com.tunit.domain.tutor.repository;

import com.tunit.domain.tutor.entity.TutorAvailableTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface TutorAvailableTimeRepository extends JpaRepository<TutorAvailableTime, Long> {
    List<TutorAvailableTime> findAllByTutorProfileNo(Long tutorProfileNo);

    boolean existsByTutorProfileNoAndDayOfWeekAndStartTimeAndEndTime(Long tutorProfileNo, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime);

    @Query("""
                SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END
                FROM TutorAvailableTime t
                WHERE t.tutorAvailableTimeNo <> :excludeId
                  AND t.tutorProfileNo = :tutorProfileNo
                  AND t.dayOfWeekNum = :dayOfWeekNum
                  AND t.startTime < :endTime
                  AND t.endTime > :startTime
            """)
    boolean existsOverlappingTime(@Param("tutorProfileNo") Long tutorProfileNo,
                                  @Param("dayOfWeekNum") Integer dayOfWeekNum,
                                  @Param("startTime") LocalTime startTime,
                                  @Param("endTime") LocalTime endTime,
                                  @Param("excludeId") Long excludeId);


    void deleteAllByTutorProfileNoAndTutorAvailableTimeNoIn(Long tutorProfileNo, List<Long> tutorAvailableTimeNos);

    boolean existsByTutorProfileNoAndTutorAvailableTimeNoIn(Long tutorProfileNo, List<Long> tutorAvailableTimeNos);
}
