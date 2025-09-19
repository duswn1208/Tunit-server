package com.tunit.domain.student.repository;

import com.tunit.domain.student.entity.StudentRegions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface StudentRegionsRepository extends JpaRepository<StudentRegions, Long> {
    Set<StudentRegions> findAllByUserNo(Long userNo);
}
