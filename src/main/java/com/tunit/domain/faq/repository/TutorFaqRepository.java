package com.tunit.domain.faq.repository;

import com.tunit.domain.faq.entity.TutorFaq;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TutorFaqRepository extends JpaRepository<TutorFaq, Long> {
    List<TutorFaq> findByTutorProfileNoAndIsExposedTrueOrderByDisplayOrderAsc(Long tutorProfileNo);
    List<TutorFaq> findByTutorProfileNoOrderByDisplayOrderAsc(Long tutorProfileNo);
    Optional<TutorFaq> findFirstByTutorProfileNoOrderByDisplayOrderDesc(Long tutorProfileNo);
    Optional<TutorFaq> findByTutorFaqNoAndTutorProfileNo(Long tutorFaqNo, Long tutorProfileNo);
}
