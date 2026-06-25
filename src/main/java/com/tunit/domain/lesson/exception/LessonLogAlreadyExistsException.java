package com.tunit.domain.lesson.exception;

public class LessonLogAlreadyExistsException extends RuntimeException {
    public LessonLogAlreadyExistsException() {
        super("이미 작성된 레슨 일지가 있습니다.");
    }
}
