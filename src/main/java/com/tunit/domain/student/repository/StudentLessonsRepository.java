package com.tunit.domain.student.repository;

import com.tunit.domain.student.entity.StudentLessons;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface StudentLessonsRepository extends JpaRepository<StudentLessons, Long> {
    Set<StudentLessons> findAllByUserNo(Long userNo);
}
