package ru.ivamly.chill.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.ivamly.chill.exception.handler.OverlappingChillException;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(OverlappingChillException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(OverlappingChillException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Существует chill в выбранные даты");
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("userId", ex.getUserId());
        problem.setProperty("startDate", ex.getStartDate());
        problem.setProperty("endDate", ex.getEndDate());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(problem);
    }
}
