package com.tunit.domain.tutor.repository;

import com.tunit.domain.tutor.entity.TutorProfile;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TutorProfileRepository extends JpaRepository<TutorProfile, Long> {
    TutorProfile findByUserNo(Long userNo);

    @EntityGraph(attributePaths = {"tutorLessons", "tutorRegions"})
    Optional<TutorProfile> findByTutorProfileNo(Long tutorProfileNo);
}
