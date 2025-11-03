package com.tunit.domain.user.exception;

public class UserException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private static final String ERROR_MESSAGE_DEFAULT = "사용자를 찾을 수 없습니다.\n고객센터로 문의 바랍니다!";

    public UserException() {
        super(ERROR_MESSAGE_DEFAULT);
    }

    public UserException(String message) {
        super(message);
    }
}
