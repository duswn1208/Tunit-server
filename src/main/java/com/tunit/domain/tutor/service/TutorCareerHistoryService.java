package com.tunit.domain.tutor.service;

import com.tunit.domain.tutor.dto.TutorCareerHistoryResponseDto;
import com.tunit.domain.tutor.dto.TutorCareerHistorySaveDto;
import com.tunit.domain.tutor.entity.TutorCareerHistory;
import com.tunit.domain.tutor.repository.TutorCareerHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TutorCareerHistoryService {

    private final TutorCareerHistoryRepository tutorCareerHistoryRepository;

    public List<TutorCareerHistoryResponseDto> findByTutorProfileNo(Long tutorProfileNo) {
        return tutorCareerHistoryRepository.findByTutorProfileNoOrderByDisplayOrderAsc(tutorProfileNo)
                .stream()
                .map(TutorCareerHistoryResponseDto::from)
                .toList();
    }

    @Transactional
    public void saveAll(Long tutorProfileNo, List<TutorCareerHistorySaveDto> dtoList) {
        tutorCareerHistoryRepository.deleteByTutorProfileNo(tutorProfileNo);
        List<TutorCareerHistory> entities = dtoList.stream()
                .map(dto -> TutorCareerHistory.saveFrom(tutorProfileNo, dto))
                .toList();
        tutorCareerHistoryRepository.saveAll(entities);
    }

    @Transactional
    public Long add(Long tutorProfileNo, TutorCareerHistorySaveDto dto) {
        TutorCareerHistory saved = tutorCareerHistoryRepository.save(TutorCareerHistory.saveFrom(tutorProfileNo, dto));
        return saved.getTutorCareerHistoryNo();
    }

    @Transactional
    public void delete(Long tutorCareerHistoryNo) {
        tutorCareerHistoryRepository.deleteById(tutorCareerHistoryNo);
    }
}
