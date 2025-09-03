package com.tunit.domain.lesson.repository;

import com.tunit.domain.lesson.dto.LessonFindResponseDto;
import com.tunit.domain.lesson.entity.LessonReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LessonReservationRepository extends JpaRepository<LessonReservation, Long> {
    boolean existsByTutorProfileNo(Long tutorProfileNo);

    @Query("SELECT new com.tunit.domain.lesson.dto.LessonFindResponseDto(lr, u.name) FROM LessonReservation lr JOIN com.tunit.domain.user.entity.UserMain u ON lr.studentNo = u.userNo WHERE lr.tutorProfileNo = :tutorProfileNo AND lr.date BETWEEN :startDate AND :endDate")
    List<LessonFindResponseDto> findByTutorProfileNoAndDateBetweenWithUser(@Param("tutorProfileNo") Long tutorProfileNo, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
