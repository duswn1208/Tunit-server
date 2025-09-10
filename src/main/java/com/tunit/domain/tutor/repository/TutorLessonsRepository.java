package com.tunit.domain.tutor.repository;

import com.tunit.domain.tutor.entity.TutorLessons;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TutorLessonsRepository extends JpaRepository<TutorLessons, Integer> {
    List<TutorLessons> findByTutorProfile_TutorProfileNo(Long tutorProfileNo);
}
