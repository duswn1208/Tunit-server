package com.tunit.domain.contract.repository;

import com.tunit.domain.contract.entity.StudentTutorContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentTutorContractRepository extends JpaRepository<StudentTutorContract, Long> {
    List<StudentTutorContract> findByStudentNo(Long studentNo);
    List<StudentTutorContract> findByTutorProfileNo(Long tutorProfileNo);
    List<StudentTutorContract> findByStudentNoAndTutorProfileNo(Long studentNo, Long tutorProfileNo);
}

