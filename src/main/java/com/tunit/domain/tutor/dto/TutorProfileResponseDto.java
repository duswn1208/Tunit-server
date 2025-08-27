package com.tunit.domain.tutor.dto;

import com.tunit.domain.lesson.define.LessonCategory;
import com.tunit.domain.tutor.entity.TutorLessons;
import com.tunit.domain.tutor.entity.TutorProfile;
import com.tunit.domain.tutor.entity.TutorRegion;

import java.util.List;

public record TutorProfileResponseDto(
        Long userNo,
        Long tutorProfileNo,
        String introduce,
        LessonCategory lessonCategory,
        List<TutorLessons> lessonSubcategoryList,
        List<TutorRegion> regionList,
        List<TutorAvailableTimeResponseDto> tutorAvailableTimeList,
        List<TutorAvailExceptionResponseDto> tutorHolidayList,
        Integer careerYears,
        Integer pricePerHour,
        Integer durationMin) {

    public static TutorProfileResponseDto from(TutorProfile tutorProfile, List<TutorAvailableTimeResponseDto> tutorAvailableTimeResponseList, List<TutorAvailExceptionResponseDto> tutorHolidayResponseList) {
        return new TutorProfileResponseDto(
                tutorProfile.getUserNo(),
                tutorProfile.getTutorProfileNo(),
                tutorProfile.getIntroduce(),
                tutorProfile.getLessonCategory(),
                tutorProfile.getTutorLessons(),
                tutorProfile.getTutorRegions(),
                tutorAvailableTimeResponseList,
                tutorHolidayResponseList,
                tutorProfile.getCareerYears(),
                tutorProfile.getPricePerHour(),
                tutorProfile.getDurationMin()
        );
    }
}
