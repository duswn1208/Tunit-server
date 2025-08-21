package com.tunit.domain.lesson.define;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tunit.domain.lesson.exception.LessonNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum LessonCategory {
    EXERCISE("운동"),
    INSTRUMENT("악기"),
    CODING("코딩"),
    LANGUAGE("외국어"),
    ART("미술"),
    MUSIC("음악"),
    COOKING("요리"),
    STUDY("학습"),
    HOBBY("취미"),
    OTHER("기타"),
    ;

    private final String label;

    public String getCode() { return name(); }
    public String getLabel() { return label; }

    public List<LessonSubCategory> getSubCategories() {
        return Arrays.stream(LessonSubCategory.values())
                .filter(sc -> sc.getParent() == this)
                .toList();
    }

    @JsonIgnore
    public static LessonCategory fromCode(String code) {
            try { return LessonCategory.valueOf(code.toUpperCase(Locale.ROOT)); }
            catch (IllegalArgumentException e) {
                throw new LessonNotFoundException("Unknown category code: " + code);
            }
    }
}
