package com.tunit.domain.lesson.exception;

public class LessonDuplicationException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private static final String ERROR_MESSAGE_DEFAULT = "동일한 날짜에 레슨이 이미 존재합니다.";

    public LessonDuplicationException() {
        super(ERROR_MESSAGE_DEFAULT);
    }

    public LessonDuplicationException(String message) {
        super(message);
    }
}
