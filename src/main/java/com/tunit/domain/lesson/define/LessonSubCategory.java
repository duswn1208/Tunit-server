package com.tunit.domain.lesson.define;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tunit.domain.lesson.exception.LessonNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum LessonSubCategory {

    PT("1:1 PT", LessonCategory.EXERCISE),
    GROUP_PT("그룹 PT", LessonCategory.EXERCISE),
    YOGA("요가", LessonCategory.EXERCISE),
    PILATES("필라테스", LessonCategory.EXERCISE),
    GUITAR("기타 악기", LessonCategory.INSTRUMENT),
    PIANO("피아노", LessonCategory.INSTRUMENT),
    DRUM("드럼", LessonCategory.INSTRUMENT),
    BASE("베이스", LessonCategory.INSTRUMENT),
    JAVASCRIPT("자바스크립트", LessonCategory.CODING),
    PYTHON("파이썬", LessonCategory.CODING),
    JAVA("자바", LessonCategory.CODING),
    ENGLISH("영어", LessonCategory.LANGUAGE),
    KOREAN("한국어", LessonCategory.LANGUAGE),
    CHINESE("중국어", LessonCategory.LANGUAGE),
    FRENCH("프랑스어", LessonCategory.LANGUAGE),
    DRAWING("드로잉", LessonCategory.ART),
    PAINTING("회화", LessonCategory.ART),
    SCULPTURE("조각", LessonCategory.ART),
    VOCAL("보컬", LessonCategory.MUSIC),
    BAKING("베이킹", LessonCategory.COOKING),
    COOKING("요리", LessonCategory.COOKING),
    ETC("기타", LessonCategory.OTHER),
    ;

    private final String label;

    @JsonIgnore
    @Getter
    private final LessonCategory parent;

    public String getCode() {
        return name();
    }

    public String getLabel() {
        return label;
    }

    public static LessonSubCategory fromName(String name) {
        try {
            return LessonSubCategory.valueOf(name);
        } catch (IllegalArgumentException e) {
            throw new LessonNotFoundException("No enum constant with name " + name);
        }
    }

    public static LessonSubCategory fromLabel(String label) {
        if (label == null) return null;
        String labelNoSpace = label.replaceAll("\\s", "");
        for (LessonSubCategory subCategory : LessonSubCategory.values()) {
            String enumLabelNoSpace = subCategory.getLabel().replaceAll("\\s", "");
            if (enumLabelNoSpace.equalsIgnoreCase(labelNoSpace)
                || enumLabelNoSpace.contains(labelNoSpace)
                || labelNoSpace.contains(enumLabelNoSpace)) {
                return subCategory;
            }
        }
        throw new LessonNotFoundException("No enum constant with label " + label);
    }
}
