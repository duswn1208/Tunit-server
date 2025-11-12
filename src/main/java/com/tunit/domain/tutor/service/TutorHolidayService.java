package com.tunit.domain.tutor.service;

import com.tunit.domain.tutor.define.TutorLessonOpenType;
import com.tunit.domain.tutor.dto.TutorAvailExceptionResponseDto;
import com.tunit.domain.tutor.dto.TutorAvailExceptionSaveDto;
import com.tunit.domain.tutor.entity.TutorAvailException;
import com.tunit.domain.tutor.exception.TutorProfileException;
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
    public void saveHoliday(Long tutorProfileNo, TutorAvailExceptionSaveDto tutorAvailExceptionSaveDto) {
        List<TutorAvailException> all = tutorAvailExceptionRepository.findByTutorProfileNo(tutorProfileNo);
        if (tutorAvailExceptionSaveDto.endDate() == null) {
            TutorAvailException entity = tutorAvailExceptionSaveDto.toEntity(tutorProfileNo);
            if (tutorAvailExceptionRepository.existsByTutorProfileNoAndDateAndType(tutorProfileNo, entity.getDate(), TutorLessonOpenType.BLOCK)) {
                throw new TutorProfileException("이미 해당 날짜에 휴일이 등록되어 있습니다. 날짜: " + entity.getDate());
            }

            tutorAvailExceptionRepository.save(entity);
            return;
        }

        LocalDate currentDate = tutorAvailExceptionSaveDto.date();
        LocalDate endDate = tutorAvailExceptionSaveDto.endDate();
        while (!currentDate.isAfter(endDate)) {
            TutorAvailException entity = TutorAvailException.from(tutorProfileNo, currentDate, tutorAvailExceptionSaveDto);
            if (tutorAvailExceptionRepository.existsByTutorProfileNoAndDateAndType(tutorProfileNo, entity.getDate(), TutorLessonOpenType.BLOCK)) {
                throw new TutorProfileException("이미 해당 날짜에 휴일이 등록되어 있습니다. 날짜: " + currentDate);
            }
            tutorAvailExceptionRepository.save(entity);
            currentDate = currentDate.plusDays(1);
        }
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
        return tutorAvailExceptionRepository.exist(
                tutorProfileNo, date, TutorLessonOpenType.BLOCK, startTime, endTime
        );
    }
}
