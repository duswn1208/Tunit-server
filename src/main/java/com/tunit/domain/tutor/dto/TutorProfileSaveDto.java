package com.tunit.domain.tutor.dto;

import com.tunit.domain.lesson.define.LessonCategory;
import com.tunit.domain.lesson.define.LessonSubCategory;
import com.tunit.domain.region.dto.RegionSaveDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class TutorProfileSaveDto {
    private Long userNo;
    private String nickname;
    private String introduce;
    private LessonCategory mainCategory;
    private List<LessonSubCategory> subCategoryList;
    private List<RegionSaveDto> regionList;
    private Integer careerYears;
    private Integer pricePerHour;
    private Integer durationMin;

    private List<TutorAvailableTimeSaveDto> tutorAvailableTimeSaveDtoList;

    @Builder(builderMethodName = "of")
    public TutorProfileSaveDto(Long userNo, String nickname, String introduce, LessonCategory mainCategory, List<LessonSubCategory> subCategoryList, List<RegionSaveDto> regionList,
                               Integer careerYears, Integer pricePerHour, Integer durationMin, List<TutorAvailableTimeSaveDto> tutorAvailableTimeSaveDtoList) {
        this.userNo = userNo;
        this.nickname = nickname;
        this.introduce = introduce;
        this.mainCategory = mainCategory;
        this.subCategoryList = subCategoryList;
        this.regionList = regionList;
        this.careerYears = careerYears;
        this.pricePerHour = pricePerHour;
        this.durationMin = durationMin;
        this.tutorAvailableTimeSaveDtoList = tutorAvailableTimeSaveDtoList;
    }

}
