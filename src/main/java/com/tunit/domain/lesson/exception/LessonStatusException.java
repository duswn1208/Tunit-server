package com.tunit.domain.lesson.exception;

public class LessonStatusException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private static final String ERROR_MESSAGE_DEFAULT = "레슨 예약 상태 오류가 발생했습니다..\n고객센터로 문의 바랍니다!";

    public LessonStatusException() {
        super(ERROR_MESSAGE_DEFAULT);
    }

    public LessonStatusException(String message) {
        super(message);
    }
}
