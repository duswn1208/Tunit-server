package com.tunit.domain.review.exception;

public class InvalidReviewException extends RuntimeException {
    private static final String DEFAULT_ERROR_MESSAGE = "리뷰 처리 중 오류가 발생했습니다.";

    public InvalidReviewException() {
        super(DEFAULT_ERROR_MESSAGE);
    }

    public InvalidReviewException(String message) {
        super(message);
    }
}

