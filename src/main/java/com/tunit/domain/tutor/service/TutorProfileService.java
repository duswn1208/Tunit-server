package com.tunit.domain.tutor.service;

import com.tunit.domain.lesson.repository.LessonReservationRepository;
import com.tunit.domain.tutor.dto.TutorProfileResponseDto;
import com.tunit.domain.tutor.dto.TutorProfileSaveDto;
import com.tunit.domain.tutor.entity.TutorProfile;
import com.tunit.domain.tutor.repository.TutorProfileRepository;
import com.tunit.domain.user.entity.UserMain;
import com.tunit.domain.user.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TutorProfileService {

    private final TutorProfileRepository tutorProfileRepository;
    private final TutorAvailableTimeService tutorAvailableTimeService;
    private final TutorHolidayService tutorHolidayService;

    public TutorProfileResponseDto findTutorProfileInfo(@NonNull Long userNo) {
        TutorProfile tutorProfile = tutorProfileRepository.findByUserNo(userNo);

        return TutorProfileResponseDto.from( tutorProfile);
    }

    @Transactional
    public Long save(Long userNo, TutorProfileSaveDto tutorProfileSaveDto) {
        TutorProfile tutorProfile = TutorProfile.saveFrom(userNo, tutorProfileSaveDto);

        TutorProfile save = tutorProfileRepository.save(tutorProfile);

        if (!tutorProfileSaveDto.getTutorAvailableTimeSaveDtoList().isEmpty()) {
            tutorAvailableTimeService.saveAvailableTime(save.getTutorProfileNo(), tutorProfileSaveDto.getTutorAvailableTimeSaveDtoList());
        }

        return save.getTutorProfileNo();
    }

    public TutorProfile findByUserNo(@NonNull Long userNo) {
        return tutorProfileRepository.findByUserNo(userNo);
    }

}
