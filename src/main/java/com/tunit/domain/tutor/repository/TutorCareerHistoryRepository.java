package com.tunit.domain.tutor.repository;

import com.tunit.domain.tutor.entity.TutorCareerHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TutorCareerHistoryRepository extends JpaRepository<TutorCareerHistory, Long> {

    List<TutorCareerHistory> findByTutorProfileNoOrderByDisplayOrderAsc(Long tutorProfileNo);

    void deleteByTutorProfileNo(Long tutorProfileNo);
}
