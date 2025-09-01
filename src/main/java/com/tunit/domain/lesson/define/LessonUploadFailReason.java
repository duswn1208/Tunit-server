package com.tunit.domain.lesson.define;

public enum LessonUploadFailReason {
    REQUIRED_FIELD_MISSING("필수값 누락"),
    USER_SIGNUP_FAIL("회원가입 실패"),
    LESSON_NOT_FOUND("레슨 정보 오류"),
    UNKNOWN_ERROR("기타 오류");

    private final String message;

    LessonUploadFailReason(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

