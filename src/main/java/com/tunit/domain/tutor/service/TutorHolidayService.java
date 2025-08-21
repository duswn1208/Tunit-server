package com.tunit.domain.tutor.service;

import com.tunit.domain.tutor.dto.TutorHolidayResponseDto;
import com.tunit.domain.tutor.dto.TutorHolidaySaveDto;
import com.tunit.domain.tutor.repository.TutorHolidayRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TutorHolidayService {

    private final TutorHolidayRepository tutorHolidayRepository;

    @Transactional
    public void saveHoliday(Long tutorProfileNo, List<TutorHolidaySaveDto> tutorHolidaySaveDtoList) {
        if (tutorHolidaySaveDtoList.isEmpty()) {
            throw new IllegalArgumentException("휴일 정보가 없습니다.");
        }

        tutorHolidayRepository.saveAll(tutorHolidaySaveDtoList.stream()
                .map(dto -> dto.toEntity(tutorProfileNo))
                .toList());
    }

    @Transactional
    public void deleteHoliday(Long tutorProfileNo, List<Long> tutorHolidayNos) {

    }

    public List<TutorHolidayResponseDto> findByTutorProfileNo(@NonNull Long tutorProfileNo) {
        return tutorHolidayRepository.findByTutorProfileNo(tutorProfileNo)
                .stream()
                .map(TutorHolidayResponseDto::from)
                .toList();
    }
}
