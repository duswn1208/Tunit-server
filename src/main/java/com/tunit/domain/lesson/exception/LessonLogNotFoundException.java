package com.tunit.domain.lesson.exception;

public class LessonLogNotFoundException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "레슨 일지를 찾을 수 없습니다.";

    public LessonLogNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public LessonLogNotFoundException(String message) {
        super(message);
    }
}
