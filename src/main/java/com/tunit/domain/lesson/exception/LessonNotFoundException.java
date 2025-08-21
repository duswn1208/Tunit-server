package com.tunit.domain.lesson.exception;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

public class LessonNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private static final String ERROR_MESSAGE_DEFAULT = "레슨을 찾을 수 없습니다.\n고객센터로 문의 바랍니다!";

    public LessonNotFoundException() {
        super(ERROR_MESSAGE_DEFAULT);
    }

    public LessonNotFoundException(String message) {
        super(message);
    }
}
