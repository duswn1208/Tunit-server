package com.tunit.common.handler;

import com.tunit.common.session.exception.SessionNotFoundException;
import com.tunit.domain.lesson.exception.LessonDuplicationException;
import com.tunit.domain.lesson.exception.LessonNotFoundException;
import com.tunit.domain.tutor.exception.TutorLessonTimeValidateException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalApiExceptionHandler {
    @ExceptionHandler(SessionNotFoundException.class)
    public ResponseEntity<?> handleSessionNotFoundException(SessionNotFoundException e) {
        return ResponseEntity.status(401).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(LessonNotFoundException.class)
    public ResponseEntity<?> handleLessonNotFoundException(LessonNotFoundException e) {
        return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(TutorLessonTimeValidateException.class)
    public ResponseEntity<?> handleTutorLessonTimeValidateException(TutorLessonTimeValidateException e) {
        return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(LessonDuplicationException.class)
    public ResponseEntity<?> handleIllegalArgumentException(LessonDuplicationException e) {
        return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
    }

}
