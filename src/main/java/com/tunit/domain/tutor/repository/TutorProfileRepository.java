package com.tunit.domain.tutor.repository;

import com.tunit.domain.lesson.define.LessonSubCategory;
import com.tunit.domain.tutor.entity.TutorProfile;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TutorProfileRepository extends JpaRepository<TutorProfile, Long> {
    TutorProfile findByUserNo(Long userNo);

    @EntityGraph(attributePaths = {"tutorLessons", "tutorRegions"})
    Optional<TutorProfile> findByTutorProfileNo(Long tutorProfileNo);

    @Query("SELECT t FROM TutorProfile t JOIN t.tutorLessons l JOIN t.tutorRegions r " +
            "WHERE l.lessonSubCategory IN :lessonSubCategories AND r.code IN :regionCodes")
    List<TutorProfile> findTutorsByCategoryAndRegion(
        List<LessonSubCategory> lessonSubCategories,
        List<Integer> regionCodes
    );
}
