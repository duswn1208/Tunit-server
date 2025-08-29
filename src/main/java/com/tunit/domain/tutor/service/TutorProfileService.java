package com.tunit.domain.tutor.service;

import com.tunit.domain.lesson.repository.LessonRepository;
import com.tunit.domain.tutor.dto.TutorProfileResponseDto;
import com.tunit.domain.tutor.dto.TutorProfileSaveDto;
import com.tunit.domain.tutor.entity.TutorProfile;
import com.tunit.domain.tutor.repository.TutorProfileRepository;
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
    private final LessonRepository lessonRepository;

    public TutorProfileResponseDto findTutorProfileInfo(@NonNull Long tutorProfileNo) {
        TutorProfile tutorProfile = tutorProfileRepository.findByTutorProfileNo(tutorProfileNo)
                .orElseThrow(() -> new IllegalArgumentException("Tutor profile not found"));

        return TutorProfileResponseDto.from(tutorProfile,
                tutorAvailableTimeService.findByTutorProfileNo(tutorProfileNo),
                tutorHolidayService.findByTutorProfileNo(tutorProfileNo));
    }

    @Transactional
    public Long save(Long userNo, TutorProfileSaveDto tutorProfileSaveDto) {
        TutorProfile tutorProfile = TutorProfile.saveFrom(tutorProfileSaveDto);

        TutorProfile save = tutorProfileRepository.save(tutorProfile);

        if (!tutorProfileSaveDto.getTutorAvailableTimeSaveDtoList().isEmpty()) {
            tutorAvailableTimeService.saveAvailableTime(save.getTutorProfileNo(), tutorProfileSaveDto.getTutorAvailableTimeSaveDtoList());
        }

        return save.getTutorProfileNo();
    }

    public TutorProfile findByUserNo(@NonNull Long userNo) {
        return tutorProfileRepository.findByUserNo(userNo);
    }

    public boolean existsLessons(@NonNull Long tutorProfileNo) {
        return lessonRepository.existsByTutorProfileNo(tutorProfileNo);
    }

    public boolean existsLessonsByUserNo(Long userNo) {
        return lessonRepository.existsByUserNo(userNo);
    }
}
