package com.tunit.domain.tutor.repository;

import com.tunit.domain.tutor.entity.TutorAvailException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TutorAvailExceptionRepository extends JpaRepository<TutorAvailException, Long> {

    List<TutorAvailException> findByTutorProfileNo(Long tutorProfileNo);
}
