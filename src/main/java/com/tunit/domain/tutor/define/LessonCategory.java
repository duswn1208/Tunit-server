package com.tunit.domain.tutor.define;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
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

    private final String title;
}
