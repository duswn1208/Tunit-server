package com.tunit.domain.user.dto;

import com.tunit.domain.lesson.define.LessonSubCategory;
import com.tunit.domain.region.dto.RegionSaveDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@NoArgsConstructor
public class StudentProfileSaveDto {
    @Setter
    private Long userNo;
    private String nickname;
    private List<LessonSubCategory> subCategoryList;
    private List<RegionSaveDto> regionList;

}
