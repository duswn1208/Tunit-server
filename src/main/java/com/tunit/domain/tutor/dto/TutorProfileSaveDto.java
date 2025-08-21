package com.tunit.domain.tutor.dto;

import com.tunit.domain.lesson.define.LessonCategory;
import com.tunit.domain.lesson.define.LessonSubCategory;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class TutorProfileSaveDto {
    private Long userNo;
    private String introduce;
    private LessonCategory lessonCategory;
    private List<LessonSubCategory> lessonSubCategoryList;
    private List<String> regionList;
    private Integer careerYears;
    private Integer pricePerHour;
    private Integer durationMin;

    private List<TutorAvailableTimeSaveDto> tutorAvailableTimeSaveDtoList;
    private List<TutorHolidaySaveDto> tutorHolidaySaveDtoList;

    @Builder(builderMethodName = "of")
    public TutorProfileSaveDto(Long userNo, String introduce, LessonCategory lessonCategory, List<LessonSubCategory> lessonSubCategoryList, List<String> regionList,
                               Integer careerYears, Integer pricePerHour, Integer durationMin, List<TutorAvailableTimeSaveDto> tutorAvailableTimeSaveDtoList, List<TutorHolidaySaveDto> tutorHolidaySaveDtoList) {
        this.userNo = userNo;
        this.introduce = introduce;
        this.lessonCategory = lessonCategory;
        this.lessonSubCategoryList = lessonSubCategoryList;
        this.regionList = regionList;
        this.careerYears = careerYears;
        this.pricePerHour = pricePerHour;
        this.durationMin = durationMin;
        this.tutorAvailableTimeSaveDtoList = tutorAvailableTimeSaveDtoList;
        this.tutorHolidaySaveDtoList = tutorHolidaySaveDtoList;
    }

}
