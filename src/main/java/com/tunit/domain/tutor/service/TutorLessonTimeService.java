package com.tunit.domain.tutor.service;

import com.tunit.domain.tutor.dto.TutorAvailableTimeSaveDto;
import com.tunit.domain.tutor.dto.TutorHolidaySaveDto;
import com.tunit.domain.tutor.entity.TutorAvailableTime;
import com.tunit.domain.tutor.entity.TutorHoliday;
import com.tunit.domain.tutor.exception.TutorProfileException;
import com.tunit.domain.tutor.repository.TutorAvailableTimeRepository;
import com.tunit.domain.tutor.repository.TutorHolidayRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TutorLessonTimeService {

    private final TutorProfileService tutorProfileService;
    private final TutorAvailableTimeRepository tutorAvailableTimeRepository;
    private final TutorHolidayRepository tutorHolidayRepository;

    @Transactional
    public void saveAvailableTime(@NonNull Long userNo, List<TutorAvailableTimeSaveDto> tutorAvailableTimeSaveDtoList) {
        if (tutorAvailableTimeSaveDtoList.isEmpty()) {
            throw new TutorProfileException("수업 가능 시간을 하나 이상 등록해야 합니다.");
        }

        Long tutorProfileNo = tutorProfileService.findByUserNo(userNo).getTutorProfileNo();

        List<TutorAvailableTime> list = tutorAvailableTimeSaveDtoList.stream().map(dto -> dto.toEntity(tutorProfileNo)).toList();
        validateAvailableTimeList(list);
        tutorAvailableTimeRepository.saveAll(list);
    }

    private void validateAvailableTimeList(List<TutorAvailableTime> list) {
        for (int i = 0; i < list.size(); i++) {
            TutorAvailableTime tutorAvailableTime = list.get(i);
            tutorAvailableTime.validateTime();

            for (int j = i + 1; j < list.size(); j++) {
                TutorAvailableTime otherAvailableTime = list.get(j);
                if (tutorAvailableTime.getDayOfWeek() != otherAvailableTime.getDayOfWeek()) continue;

                // 겹치는지 확인
                if (tutorAvailableTime.getStartTime().isBefore(otherAvailableTime.getEndTime()) &&
                        otherAvailableTime.getStartTime().isBefore(tutorAvailableTime.getEndTime())) {
                    throw new TutorProfileException("같은 요일에 겹치는 시간이 존재합니다.");
                }
            }
        }
    }

    public List<TutorAvailableTime> findAvailableTimeByUserNo(Long userNo) {
        Long tutorProfileNo = tutorProfileService.findByUserNo(userNo).getTutorProfileNo();
        return tutorAvailableTimeRepository.findAllByTutorProfileNo(tutorProfileNo);
    }

    @Transactional
    public void saveHoliday(@NonNull Long userNo, List<TutorHolidaySaveDto> tutorHolidaySaveDtoList) {
        Long tutorProfileNo = tutorProfileService.findByUserNo(userNo).getTutorProfileNo();

        List<TutorHoliday> list = tutorHolidaySaveDtoList.stream().map(dto -> dto.toEntity(tutorProfileNo)).toList();
        tutorHolidayRepository.saveAll(list);
    }

}
