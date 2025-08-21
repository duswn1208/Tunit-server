package com.tunit.domain.tutor.dto;

import com.tunit.domain.tutor.define.LessonCategory;
import com.tunit.domain.tutor.define.LessonSubcategory;
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
    private List<LessonSubcategory> lessonSubcategoryList;
    private List<String> regionList;
    private Integer careerYears;
    private Integer pricePerHour;
    private Integer durationMin;

    private List<TutorAvailableTimeSaveDto> tutorAvailableTimeSaveDtoList;
    private List<TutorHolidaySaveDto> tutorHolidaySaveDtoList;

    @Builder(builderMethodName = "of")
    public TutorProfileSaveDto(Long userNo, String introduce, LessonCategory lessonCategory, List<LessonSubcategory> lessonSubcategoryList, List<String> regionList,
                               Integer careerYears, Integer pricePerHour, Integer durationMin, List<TutorAvailableTimeSaveDto> tutorAvailableTimeSaveDtoList, List<TutorHolidaySaveDto> tutorHolidaySaveDtoList) {
        this.userNo = userNo;
        this.introduce = introduce;
        this.lessonCategory = lessonCategory;
        this.lessonSubcategoryList = lessonSubcategoryList;
        this.regionList = regionList;
        this.careerYears = careerYears;
        this.pricePerHour = pricePerHour;
        this.durationMin = durationMin;
        this.tutorAvailableTimeSaveDtoList = tutorAvailableTimeSaveDtoList;
        this.tutorHolidaySaveDtoList = tutorHolidaySaveDtoList;
    }

}
