package com.tunit.domain.lesson.repository;

import com.tunit.domain.lesson.define.ReservationStatus;
import com.tunit.domain.lesson.dto.LessonResponsDto;
import com.tunit.domain.lesson.entity.LessonReservation;
import com.tunit.domain.student.dto.StudentLessonResponseDto;
import org.springframework.data.domain.Pageable;
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

    boolean existsByStudentNoAndDateAndStartTimeAndEndTimeAndStatusInAndLessonReservationNoNot(
            Long studentNo,
            LocalDate lessonDate,
            LocalTime startTime,
            LocalTime endTime,
            List<ReservationStatus> validLessonStatuses,
            Long lessonReservationNo);

    // 계약의 전체 레슨 개수
    long countByContractNo(Long contractNo);

    // 계약의 최근 n개 레슨(날짜/시간 내림차순)
    @Query("SELECT lr FROM LessonReservation lr WHERE lr.contractNo = :contractNo ORDER BY lr.date DESC, lr.startTime DESC")
    List<LessonReservation> findTopNByContractNoOrderByDateDescStartTimeDesc(
            @Param("contractNo") Long contractNo,
            Pageable pageable);
}

