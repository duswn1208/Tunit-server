package com.tunit.domain.tutor.service;

import com.tunit.domain.lesson.define.LessonSubCategory;
import com.tunit.domain.lesson.entity.LessonReservation;
import com.tunit.domain.lesson.exception.LessonNotFoundException;
import com.tunit.domain.tutor.define.SortType;
import com.tunit.domain.tutor.dto.*;
import com.tunit.domain.tutor.entity.TutorProfile;
import com.tunit.domain.tutor.entity.TutorRegion;
import com.tunit.domain.tutor.repository.TutorProfileRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TutorProfileService {

    private final TutorProfileRepository tutorProfileRepository;
    private final TutorAvailableTimeService tutorAvailableTimeService;
    private final TutorHolidayService tutorHolidayService;

    public TutorProfileResponseDto findTutorProfileInfo(@NonNull Long userNo) {
        TutorProfile tutorProfile = tutorProfileRepository.findByUserNo(userNo);

        return TutorProfileResponseDto.from(tutorProfile);
    }

    public TutorProfileResponseDto findTutorProfileInfoByTutorProfileNo(@NonNull Long tutorProfileNo) {
        TutorProfile tutorProfile = tutorProfileRepository.findByTutorProfileNo(tutorProfileNo)
                .orElseThrow();
        // userMain이 TutorProfile에 없으므로 nickname은 null로 처리
        List<TutorAvailableTimeResponseDto> availableTimes = tutorAvailableTimeService.findByTutorProfileNo(tutorProfileNo);
        return TutorProfileResponseDto.from(tutorProfile,  availableTimes, null);
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
        List<LessonSubCategory> lessonCodes = tutorFindRequestDto.getLessonCodes();
        List<Integer> regionCodes = tutorFindRequestDto.getRegionCodes();
        SortType sortType = tutorFindRequestDto.getSortType();

        if (lessonCodes != null && lessonCodes.isEmpty()) lessonCodes = null;
        if (regionCodes != null && regionCodes.isEmpty()) regionCodes = null;

        List<Long> tutorProfileNos = tutorProfileRepository.findTutorProfileNoByCategoryAndRegion(lessonCodes, regionCodes);
        if (tutorProfileNos.isEmpty()) {
            return List.of();
        }

        List<TutorProfile> profileList = tutorProfileRepository.findByTutorProfileNoIn(tutorProfileNos);
        List<TutorProfileResponseDto> result = profileList.stream().map(TutorProfileResponseDto::from).toList();

        if (sortType != null) {
            switch (sortType) {
                case LATEST -> result = result.stream()
                        .sorted((a, b) -> b.tutorProfileNo().compareTo(a.tutorProfileNo())) // 최신 등록순(내림차순)
                        .toList();
                case PRICE_LOW -> result = result.stream()
                        .sorted((a, b) -> Integer.compare(a.pricePerHour(), b.pricePerHour()))
                        .toList();
                case PRICE_HIGH -> result = result.stream()
                        .sorted((a, b) -> Integer.compare(b.pricePerHour(), a.pricePerHour()))
                        .toList();
                // REVIEW(후기 많은 순)는 추후 구현
            }
        }
        return result;
    }
}
