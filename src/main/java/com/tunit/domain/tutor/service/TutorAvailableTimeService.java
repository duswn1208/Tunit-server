package com.tunit.domain.tutor.service;

import com.tunit.domain.tutor.dto.TutorAvailableTimeResponseDto;
import com.tunit.domain.tutor.dto.TutorAvailableTimeSaveDto;
import com.tunit.domain.tutor.entity.TutorAvailableTime;
import com.tunit.domain.tutor.exception.TutorProfileException;
import com.tunit.domain.tutor.repository.TutorAvailableTimeRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TutorAvailableTimeService {

    private final TutorAvailableTimeRepository tutorAvailableTimeRepository;

    public boolean isWithinAvailableTime(Long tutorProfileNo, Integer dayOfWeekNum, LocalTime startTime, LocalTime endTime) {
        return tutorAvailableTimeRepository.existsByTutorProfileNoAndDayOfWeekNumAndStartTimeGreaterThanEqualAndEndTimeLessThanEqual(
                tutorProfileNo, dayOfWeekNum, startTime, endTime
        );
    }

    @Transactional
    public List<TutorAvailableTime> saveAvailableTime(@NonNull Long tutorProfileNo, List<TutorAvailableTimeSaveDto> tutorAvailableTimeSaveDtoList) {
        if (tutorAvailableTimeSaveDtoList.isEmpty()) {
            throw new TutorProfileException("수업 가능 시간을 하나 이상 등록해야 합니다.");
        }

        List<TutorAvailableTime> list = tutorAvailableTimeSaveDtoList.stream().map(dto -> dto.toEntity(tutorProfileNo)).toList();
        validateAvailableTimeList(list);
        return tutorAvailableTimeRepository.saveAll(list);
    }

    public void deleteAllByTutorProfileNo(@NonNull Long tutorProfileNo) {
        tutorAvailableTimeRepository.deleteAllByTutorProfileNo(tutorProfileNo);
    }

    private void validateAvailableTimeList(List<TutorAvailableTime> list) {
        for (int i = 0; i < list.size(); i++) {
            TutorAvailableTime tutorAvailableTime = list.get(i);
            tutorAvailableTime.validateTime();

            for (int j = i + 1; j < list.size(); j++) {
                TutorAvailableTime otherAvailableTime = list.get(j);
                if (tutorAvailableTime.getDayOfWeek() != otherAvailableTime.getDayOfWeek()) continue;

                // 겹치는지 확인
                if (tutorAvailableTime.getDayOfWeekNum().equals(otherAvailableTime.getDayOfWeekNum())) {
                    if (tutorAvailableTime.getStartTime().isBefore(otherAvailableTime.getEndTime()) &&
                            otherAvailableTime.getStartTime().isBefore(tutorAvailableTime.getEndTime())) {
                        throw new TutorProfileException("같은 요일에 겹치는 시간이 존재합니다.");
                    }
                }
            }
        }
    }

    public List<TutorAvailableTime> findAvailableTimeByTutorProfileNo(Long tutorProfileNo) {
        return tutorAvailableTimeRepository.findAllByTutorProfileNo(tutorProfileNo);
    }

    public List<TutorAvailableTimeResponseDto> findByTutorProfileNo(@NonNull Long tutorProfileNo) {
        return tutorAvailableTimeRepository.findAllByTutorProfileNo(tutorProfileNo)
                .stream()
                .map(TutorAvailableTimeResponseDto::from)
                .toList();
    }

    public boolean existsByTutorProfileNoAndDayOfWeekNumAndRequestTimeBetweenStartTimeAndEndTime(
            Long tutorProfileNo,
            Integer dayOfWeekNum,
            LocalTime requestTime
    ) {
        return tutorAvailableTimeRepository.existsByTutorProfileNoAndDayOfWeekNumAndRequestTimeBetweenStartTimeAndEndTime(
                tutorProfileNo,
                dayOfWeekNum,
                requestTime
        );
    }
}
