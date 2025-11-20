package com.tunit.domain.review.exception;

public class ReviewAlreadyExistsException extends InvalidReviewException {
    private static final String DEFAULT_ERROR_MESSAGE = "이미 리뷰를 작성했습니다.";

    public ReviewAlreadyExistsException() {
        super(DEFAULT_ERROR_MESSAGE);
    }

    public ReviewAlreadyExistsException(String message) {
        super(message);
    }
}

