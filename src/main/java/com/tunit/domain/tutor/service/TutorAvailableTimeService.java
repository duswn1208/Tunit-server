package com.tunit.domain.tutor.service;

import com.tunit.domain.tutor.dto.TutorAvailableTimeSaveDto;
import com.tunit.domain.tutor.dto.TutorAvailableTimeUpdateDto;
import com.tunit.domain.tutor.entity.TutorAvailableTime;
import com.tunit.domain.tutor.entity.TutorProfile;
import com.tunit.domain.tutor.exception.TutorProfileException;
import com.tunit.domain.tutor.repository.TutorAvailableTimeRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TutorAvailableTimeService {

    private final TutorProfileService tutorProfileService;
    private final TutorAvailableTimeRepository tutorAvailableTimeRepository;

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

    @Transactional
    public void deleteAvailableTime(@NonNull Long userNo, List<Long> tutorAvailableTimeNos) {
        if (tutorAvailableTimeNos.isEmpty()) {
            throw new TutorProfileException("삭제할 수업 정보를 선택해주세요.");
        }

        TutorProfile tutorProfile = tutorProfileService.findByUserNo(userNo);
        if (!tutorAvailableTimeRepository.existsByTutorProfileNoAndTutorAvailableTimeNoIn(tutorProfile.getTutorProfileNo(), tutorAvailableTimeNos)) {
            throw new TutorProfileException("존재하지 않는 시간대입니다.");
        }

        tutorAvailableTimeRepository.deleteAllByTutorProfileNoAndTutorAvailableTimeNoIn(tutorProfile.getTutorProfileNo(), tutorAvailableTimeNos);
    }

    @Transactional
    public void updateAvailableTime(@NonNull Long userNo, TutorAvailableTimeUpdateDto updateDto) {
        TutorProfile tutorProfile = tutorProfileService.findByUserNo(userNo);
        Long tutorProfileNo = tutorProfile.getTutorProfileNo();

        TutorAvailableTime time = getTutorAvailableTime(updateDto.getTutorAvailableTimeNo());

        time.validateTime();

        // 2. 겹치는 시간대가 있는지 확인 (본인 것만)
        boolean overlaps = tutorAvailableTimeRepository.existsOverlappingTime(
                tutorProfileNo,
                updateDto.getDayOfWeek(),
                updateDto.getStartTime(),
                updateDto.getEndTime(),
                updateDto.getTutorAvailableTimeNo()
        );
        if (overlaps) {
            throw new TutorProfileException("겹치는 시간대가 이미 존재합니다.");
        }

        time.update(
                updateDto.getDayOfWeek(),
                updateDto.getStartTime(),
                updateDto.getEndTime()
        );
    }

    public TutorAvailableTime getTutorAvailableTime(Long tutorAvailableTimeNo) {
        return tutorAvailableTimeRepository.findById(tutorAvailableTimeNo)
                .orElseThrow(() -> new TutorProfileException("존재하지 않는 시간대입니다."));
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

}
