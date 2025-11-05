package com.tunit.domain.lesson.repository;

import com.tunit.domain.lesson.define.ReservationStatus;
import com.tunit.domain.lesson.dto.LessonResponsDto;
import com.tunit.domain.lesson.entity.LessonReservation;
import com.tunit.domain.student.dto.StudentLessonResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface LessonReservationRepository extends JpaRepository<LessonReservation, Long> {
    boolean existsByTutorProfileNo(Long tutorProfileNo);

    @Query("SELECT new com.tunit.domain.lesson.dto.LessonResponsDto(lr, u.name) FROM LessonReservation lr JOIN UserMain u ON lr.studentNo = u.userNo WHERE lr.tutorProfileNo = :tutorProfileNo AND lr.date BETWEEN :startDate AND :endDate")
    List<LessonResponsDto> findByTutorProfileNoAndDateBetweenWithUser(
            @Param("tutorProfileNo") Long tutorProfileNo,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    boolean existsByTutorProfileNoAndDateAndStartTimeAndEndTimeAndStatusIn(
            Long tutorProfileNo,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            List<ReservationStatus> statuses);

    List<LessonReservation> findByTutorProfileNoAndDateBetweenAndStatusIn(
            Long tutorProfileNo,
            LocalDate startDate,
            LocalDate endDate,
            List<ReservationStatus> statuses);

    @Query("SELECT lr FROM LessonReservation lr " +
           "WHERE lr.studentNo = :studentNo " +
           "AND (:contractNo IS NULL OR lr.contractNo = :contractNo) " +
           "AND lr.date BETWEEN :startDate AND :endDate " +
           "ORDER BY lr.date ASC, lr.startTime ASC")
    List<LessonReservation> findByStudentNoAndOptionalContractNoAndDateBetween(
            @Param("studentNo") Long studentNo,
            @Param("contractNo") Long contractNo,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    Optional<LessonReservation> findByContractNo(Long contractNo);
}
