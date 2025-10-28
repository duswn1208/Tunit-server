package com.tunit.domain.tutor.dto;

import com.tunit.domain.lesson.define.LessonCategory;
import com.tunit.domain.region.dto.RegionSaveDto;
import com.tunit.domain.tutor.entity.TutorLessons;
import com.tunit.domain.tutor.entity.TutorProfile;
import com.tunit.domain.tutor.entity.TutorRegion;
import com.tunit.domain.user.dto.UserMainResponseDto;
import com.tunit.domain.user.entity.UserMain;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record TutorProfileResponseDto(
        Long userNo,
        Long tutorProfileNo,
        String introduce,
        LessonCategory lessonCategory,
        Set<TutorLessonsResponseDto> lessonSubcategoryList,
        Set<RegionSaveDto> regionList,
        List<TutorAvailableTimeResponseDto> tutorAvailableTimeList,
        List<TutorAvailExceptionResponseDto> tutorHolidayList,
        Integer careerYears,
        Integer pricePerHour,
        Integer durationMin) {

    public static TutorProfileResponseDto from(TutorProfile tutorProfile) {
        return new TutorProfileResponseDto(
                tutorProfile.getUserNo(),
                tutorProfile.getTutorProfileNo(),
                tutorProfile.getIntroduce(),
                tutorProfile.getLessonCategory(),
                tutorProfile.getTutorLessons().stream().map(TutorLessonsResponseDto::from).collect(Collectors.toSet()),
                tutorProfile.getTutorRegions().stream().map(RegionSaveDto::from).collect(Collectors.toSet()),
                List.of(),
                List.of(),
                tutorProfile.getCareerYears(),
                tutorProfile.getPricePerHour(),
                tutorProfile.getDurationMin()
        );
    }

    public static TutorProfileResponseDto from(TutorProfile tutorProfile, List<TutorAvailableTimeResponseDto> tutorAvailableTimeResponseList, List<TutorAvailExceptionResponseDto> tutorHolidayResponseList) {
        return new TutorProfileResponseDto(
                tutorProfile.getUserNo(),
                tutorProfile.getTutorProfileNo(),
                tutorProfile.getIntroduce(),
                tutorProfile.getLessonCategory(),
                tutorProfile.getTutorLessons().stream().map(TutorLessonsResponseDto::from).collect(Collectors.toSet()),
                tutorProfile.getTutorRegions().stream().map(RegionSaveDto::from).collect(Collectors.toSet()),
                tutorAvailableTimeResponseList,
                tutorHolidayResponseList,
                tutorProfile.getCareerYears(),
                tutorProfile.getPricePerHour(),
                tutorProfile.getDurationMin()
        );
    }

    public static TutorProfileResponseDto simpleProfileInfo(TutorProfileResponseDto tutorProfile) {
        return new TutorProfileResponseDto(
                tutorProfile.userNo,
                tutorProfile.tutorProfileNo,
                tutorProfile.introduce,
                tutorProfile.lessonCategory,
                tutorProfile.lessonSubcategoryList,
                Set.of(),
                List.of(),
                List.of(),
                null,
                null,
                null
        );
    }
}
