package com.tunit.common.session.exception;

public class SessionNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private static final String ERROR_MESSAGE_DEFAULT = "로그인해주세요.";

    public SessionNotFoundException() {
        super(ERROR_MESSAGE_DEFAULT);
    }

    public SessionNotFoundException(String message) {
        super(message);
    }
}
