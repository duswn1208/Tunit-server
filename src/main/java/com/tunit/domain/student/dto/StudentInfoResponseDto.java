package com.tunit.domain.student.dto;

import com.tunit.domain.student.entity.StudentLessons;
import com.tunit.domain.student.entity.StudentRegions;

import java.util.Set;

public record StudentInfoResponseDto(
        Long userNo,
        Set<StudentLessons> lessonSubcategoryList,
        Set<StudentRegions> regionList
        ) {

    public static StudentInfoResponseDto from(Long userNo, Set<StudentLessons> lessonSubcategoryList, Set<StudentRegions> regionList) {
        return new StudentInfoResponseDto(
                userNo,
                lessonSubcategoryList,
                regionList
        );
    }
}
