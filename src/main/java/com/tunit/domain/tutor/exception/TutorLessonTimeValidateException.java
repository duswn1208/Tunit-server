package com.tunit.domain.tutor.exception;

import com.tunit.domain.user.exception.UserException;

public class TutorLessonTimeValidateException extends UserException {
    private static final long serialVersionUID = 1L;
    private static final String ERROR_MESSAGE_DEFAULT = "예약 가능한 시간이 아닙니다.";

    public TutorLessonTimeValidateException() {
        super(ERROR_MESSAGE_DEFAULT);
    }

    public TutorLessonTimeValidateException(String message) {
        super(message);
    }
}
