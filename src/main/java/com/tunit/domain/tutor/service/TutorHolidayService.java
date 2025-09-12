package com.tunit.domain.tutor.service;

import com.tunit.domain.tutor.dto.TutorAvailExceptionResponseDto;
import com.tunit.domain.tutor.dto.TutorAvailExceptionSaveDto;
import com.tunit.domain.tutor.entity.TutorAvailException;
import com.tunit.domain.tutor.repository.TutorAvailExceptionRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TutorHolidayService {

    private final TutorAvailExceptionRepository tutorAvailExceptionRepository;

    @Transactional
    public void saveHoliday(Long tutorProfileNo, List<TutorAvailExceptionSaveDto> tutorAvailExceptionSaveDtoList) {
        if (tutorAvailExceptionSaveDtoList.isEmpty()) {
            throw new IllegalArgumentException("휴일 정보가 없습니다.");
        }

        tutorAvailExceptionRepository.saveAll(tutorAvailExceptionSaveDtoList.stream()
                .map(dto -> dto.toEntity(tutorProfileNo))
                .toList());
    }

    @Transactional
    public void deleteHoliday(Long tutorProfileNo, List<Long> tutorHolidayNos) {

    }

    public List<TutorAvailException> findHolidaysByTutorProfileNo(Long tutorProfileNo) {
        return tutorAvailExceptionRepository.findByTutorProfileNo(tutorProfileNo);
    }

    public List<TutorAvailExceptionResponseDto> findByTutorProfileNo(@NonNull Long tutorProfileNo) {
        return tutorAvailExceptionRepository.findByTutorProfileNo(tutorProfileNo)
                .stream()
                .map(TutorAvailExceptionResponseDto::from)
                .toList();
    }

    public boolean isWhithinHoliday(Long tutorProfileNo, LocalDate date, LocalTime startTime, LocalTime endTime) {
        return tutorAvailExceptionRepository.existsByTutorProfileNoAndDateAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                tutorProfileNo, date, startTime, endTime
        );
    }
}
