package com.tunit.domain.tutor.exception;

import com.tunit.domain.user.exception.UserException;

public class TutorProfileException extends UserException {
    private static final long serialVersionUID = 1L;
    private static final String ERROR_MESSAGE_DEFAULT = "튜터를 찾을 수 없습니다.\n고객센터로 문의 바랍니다!";

    public TutorProfileException() {
        super(ERROR_MESSAGE_DEFAULT);
    }

    public TutorProfileException(String message) {
        super(message);
    }
}
