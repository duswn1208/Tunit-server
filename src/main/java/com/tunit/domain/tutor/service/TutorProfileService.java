package com.tunit.domain.tutor.service;

import com.tunit.domain.lesson.entity.LessonReservation;
import com.tunit.domain.lesson.exception.LessonNotFoundException;
import com.tunit.domain.tutor.dto.TutorFindRequestDto;
import com.tunit.domain.tutor.dto.TutorLessonsResponseDto;
import com.tunit.domain.tutor.dto.TutorProfileResponseDto;
import com.tunit.domain.tutor.dto.TutorProfileSaveDto;
import com.tunit.domain.tutor.entity.TutorProfile;
import com.tunit.domain.tutor.repository.TutorProfileRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TutorProfileService {

    private final TutorProfileRepository tutorProfileRepository;
    private final TutorAvailableTimeService tutorAvailableTimeService;
    private final TutorHolidayService tutorHolidayService;

    public TutorProfileResponseDto findTutorProfileInfo(@NonNull Long userNo) {
        TutorProfile tutorProfile = tutorProfileRepository.findByUserNo(userNo);

        return TutorProfileResponseDto.from(tutorProfile);
    }

    public TutorProfileResponseDto findTutorProfileInfoByTutorProfileNo(@NonNull Long tutorProfileNo) {
        TutorProfile tutorProfile = tutorProfileRepository.findById(tutorProfileNo).orElseThrow();

        return TutorProfileResponseDto.from(tutorProfile);
    }

    public List<TutorLessonsResponseDto> findTutorLessonsByTutorProfileNo(@NonNull Long tutorProfileNo) {
        TutorProfile tutorProfile = tutorProfileRepository.findById(tutorProfileNo).orElseThrow();
        List<TutorLessonsResponseDto> tutorLessons = tutorProfile.getTutorLessons().stream().map(TutorLessonsResponseDto::from).toList();

        return tutorLessons;
    }

    public void checkBusinessAndHolidays(LessonReservation lessonReservation) {
        boolean isAvailable = tutorAvailableTimeService.isWithinAvailableTime(
                lessonReservation.getTutorProfileNo(),
                lessonReservation.getDayOfWeekNum(),
                lessonReservation.getStartTime(),
                lessonReservation.getEndTime()
        );
        if (!isAvailable) {
            throw new LessonNotFoundException("레슨 예약은 영업 시간 내로 가능합니다.");
        }

        boolean isHoliday = tutorHolidayService.isWhithinHoliday(lessonReservation.getTutorProfileNo(), lessonReservation.getDate(), lessonReservation.getStartTime(), lessonReservation.getEndTime());
        if (isHoliday) {
            throw new LessonNotFoundException("레슨 날짜가 휴무일입니다.");
        }
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

    public List<TutorProfileResponseDto> findTutors(TutorFindRequestDto tutorFindRequestDto) {
        List<TutorProfile> profileList = tutorProfileRepository.findTutorsByCategoryAndRegion(tutorFindRequestDto.getLessonCodes(), tutorFindRequestDto.getRegionCodes());

        return profileList.stream().map(TutorProfileResponseDto::from).toList();
    }
}
