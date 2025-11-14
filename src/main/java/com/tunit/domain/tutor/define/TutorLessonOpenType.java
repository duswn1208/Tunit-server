package com.tunit.domain.tutor.define;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
@RequiredArgsConstructor
public enum TutorLessonOpenType {
    BLOCK("BLOCK", "휴일"),
    OPEN("OPEN", "레슨일");

    private final String code;
    private final String label;
}
