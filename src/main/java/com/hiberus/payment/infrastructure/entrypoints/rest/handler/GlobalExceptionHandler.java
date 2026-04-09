package com.hiberus.payment.infrastructure.entrypoints.rest.handler;

import com.hiberus.payment.domain.exception.IdempotencyException;
import com.hiberus.payment.domain.exception.PaymentOrderNotFoundException;
import com.hiberus.payment.infrastructure.entrypoints.rest.dto.ProblemDetail;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PaymentOrderNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(PaymentOrderNotFoundException ex) {
        ProblemDetail problem = new ProblemDetail();
        problem.setStatus(HttpStatus.NOT_FOUND.value());
        problem.setTitle("Payment Order Not Found");
        problem.setDetail(ex.getMessage());
        problem.setType("https://example.com/probs/not-found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @ExceptionHandler(IdempotencyException.class)
    public ResponseEntity<ProblemDetail> handleIdempotency(IdempotencyException ex) {
        ProblemDetail problem = new ProblemDetail();
        problem.setStatus(HttpStatus.BAD_REQUEST.value());
        problem.setTitle("Idempotency Concept Violated");
        problem.setDetail(ex.getMessage());
        problem.setType("https://example.com/probs/idempotency");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgument(IllegalArgumentException ex) {
        ProblemDetail problem = new ProblemDetail();
        problem.setStatus(HttpStatus.BAD_REQUEST.value());
        problem.setTitle("Bad Request");
        problem.setDetail(ex.getMessage());
        problem.setType("https://example.com/probs/bad-request");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneralException(Exception ex) {
        ProblemDetail problem = new ProblemDetail();
        problem.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        problem.setTitle("Internal Server Error");
        problem.setDetail(ex.getMessage());
        problem.setType("https://example.com/probs/internal-error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }
}
