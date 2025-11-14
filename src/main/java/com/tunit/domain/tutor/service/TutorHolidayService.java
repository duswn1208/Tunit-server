package com.tunit.domain.tutor.service;

import com.tunit.domain.tutor.define.TutorLessonOpenType;
import com.tunit.domain.tutor.dto.TutorAvailExceptionResponseDto;
import com.tunit.domain.tutor.dto.TutorAvailExceptionSaveDto;
import com.tunit.domain.tutor.entity.TutorAvailException;
import com.tunit.domain.tutor.exception.TutorProfileException;
import com.tunit.domain.tutor.repository.TutorAvailExceptionRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TutorHolidayService {

    private final TutorAvailExceptionRepository tutorAvailExceptionRepository;

    @Transactional
    public void saveHoliday(Long tutorProfileNo, TutorAvailExceptionSaveDto tutorAvailExceptionSaveDto) {
        if (tutorAvailExceptionSaveDto.endDate() == null) {
            TutorAvailException entity = tutorAvailExceptionSaveDto.toEntity(tutorProfileNo);
            validateAndSaveHoliday(tutorProfileNo, entity.getDate(), entity);
            return;
        }

        LocalDate currentDate = tutorAvailExceptionSaveDto.date();
        LocalDate endDate = tutorAvailExceptionSaveDto.endDate();
        while (!currentDate.isAfter(endDate)) {
            TutorAvailException entity = TutorAvailException.from(tutorProfileNo, currentDate, tutorAvailExceptionSaveDto);
            validateAndSaveHoliday(tutorProfileNo, currentDate, entity);
            currentDate = currentDate.plusDays(1);
        }
    }

    private void validateAndSaveHoliday(Long tutorProfileNo, LocalDate currentDate, TutorAvailException entity) {
        existsHolidayDate(tutorProfileNo, currentDate);
        tutorAvailExceptionRepository.save(entity);
    }

    private void existsHolidayDate(Long tutorProfileNo, LocalDate date) {
        if (tutorAvailExceptionRepository.existsByTutorProfileNoAndDateAndType(tutorProfileNo, date, TutorLessonOpenType.BLOCK)) {
            throw new TutorProfileException("이미 해당 날짜에 휴일이 등록되어 있습니다. 날짜: " + date);
        }
    }

    @Transactional
    public void deleteHoliday(Long tutorProfileNo, Long tutorHolidayNo) {
        Integer result = tutorAvailExceptionRepository.deleteByTutorAvailExceptionNoAndTutorProfileNo(tutorHolidayNo, tutorProfileNo);
        if (result == 0) {
            throw new TutorProfileException("삭제할 휴일이 존재하지 않습니다. tutorHolidayNo: " + tutorHolidayNo);
        }

        log.info("휴일 삭제 완료. tutorHolidayNo: {}, tutorProfileNo: {}", tutorHolidayNo, tutorProfileNo);
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
