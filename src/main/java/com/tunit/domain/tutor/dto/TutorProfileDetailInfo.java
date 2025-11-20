package com.tunit.domain.tutor.dto;

import com.tunit.domain.lesson.define.LessonCategory;
import com.tunit.domain.region.dto.RegionSaveDto;
import com.tunit.domain.tutor.entity.TutorProfile;
import com.tunit.domain.user.dto.UserMainResponseDto;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record TutorProfileDetailInfo(
        Long userNo,
        Long tutorProfileNo,
        UserMainResponseDto userInfo,
        String introduce,
        LessonCategory lessonCategory,
        Set<TutorLessonsResponseDto> lessonSubcategoryList,
        Set<RegionSaveDto> regionList,
        List<TutorAvailableTimeResponseDto> tutorAvailableTimeList,
        List<TutorAvailExceptionResponseDto> tutorHolidayList,
        Integer careerYears,
        Integer pricePerHour,
        Integer durationMin,
        Double averageRating,
        Long totalReviews
) {

    //static from method
    public static TutorProfileDetailInfo from(TutorProfile tutorProfile, UserMainResponseDto userMain, List<TutorAvailableTimeResponseDto> tutorAvailableTimeResponseList,
                                              List<TutorAvailExceptionResponseDto> tutorHolidayResponseList,
                                              Double averageRating, Long totalReviews) {
        return new TutorProfileDetailInfo(
                tutorProfile.getUserNo(),
                tutorProfile.getTutorProfileNo(),
                userMain,
                tutorProfile.getIntroduce(),
                tutorProfile.getLessonCategory(),
                tutorProfile.getTutorLessons().stream().map(TutorLessonsResponseDto::from).collect(Collectors.toSet()),
                tutorProfile.getTutorRegions().stream().map(RegionSaveDto::from).collect(Collectors.toSet()),
                tutorAvailableTimeResponseList,
                tutorHolidayResponseList,
                tutorProfile.getCareerYears(),
                tutorProfile.getPricePerHour(),
                tutorProfile.getDurationMin(),
                averageRating,
                totalReviews
        );
    }
}
