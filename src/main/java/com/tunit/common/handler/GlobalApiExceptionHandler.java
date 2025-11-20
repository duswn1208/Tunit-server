package com.tunit.common.handler;

import com.tunit.common.session.exception.SessionNotFoundException;
import com.tunit.domain.lesson.exception.LessonDuplicationException;
import com.tunit.domain.lesson.exception.LessonNotFoundException;
import com.tunit.domain.review.exception.InvalidReviewException;
import com.tunit.domain.review.exception.ReviewAlreadyExistsException;
import com.tunit.domain.review.exception.ReviewNotFoundException;
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

    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<?> handleReviewNotFoundException(ReviewNotFoundException e) {
        return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(ReviewAlreadyExistsException.class)
    public ResponseEntity<?> handleReviewAlreadyExistsException(ReviewAlreadyExistsException e) {
        return ResponseEntity.status(409).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(InvalidReviewException.class)
    public ResponseEntity<?> handleInvalidReviewException(InvalidReviewException e) {
        return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
    }
}
