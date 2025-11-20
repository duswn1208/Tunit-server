package com.tunit.domain.review.exception;

public class ReviewNotFoundException extends InvalidReviewException {

    private static final String DEFAULT_ERROR_MESSAGE = "리뷰를 찾을 수 없습니다.";

    public ReviewNotFoundException() {
        super(DEFAULT_ERROR_MESSAGE);
    }

    public ReviewNotFoundException(String message) {
        super(message);
    }
}

