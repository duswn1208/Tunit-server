package com.tunit.domain.faq.exception;

public class TutorFaqException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private static final String ERROR_MESSAGE_DEFAULT = "튜터 FAQ 처리에 실패했습니다. 고객센터 문의부탁드립니다.";

    public TutorFaqException() {
        super(ERROR_MESSAGE_DEFAULT);
    }

    public TutorFaqException(String message) {
        super(message);
    }
}
