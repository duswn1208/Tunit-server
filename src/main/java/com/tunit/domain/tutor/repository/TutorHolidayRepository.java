package com.tunit.domain.tutor.repository;

import com.tunit.domain.tutor.entity.TutorHoliday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TutorHolidayRepository extends JpaRepository<TutorHoliday, Long> {

    List<TutorHoliday> findByTutorProfileNo(Long tutorProfileNo);
}
