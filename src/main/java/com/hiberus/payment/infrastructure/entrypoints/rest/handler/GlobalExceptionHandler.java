package com.hiberus.payment.infrastructure.entrypoints.rest.handler;

import com.hiberus.payment.domain.exception.IdempotencyException;
import com.hiberus.payment.domain.exception.PaymentOrderNotFoundException;
import com.hiberus.payment.infrastructure.entrypoints.rest.dto.ModelApiResponse;
import com.hiberus.payment.infrastructure.entrypoints.rest.constants.ApiConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PaymentOrderNotFoundException.class)
    public ResponseEntity<ModelApiResponse> handleNotFound(PaymentOrderNotFoundException ex) {
        ModelApiResponse response = new ModelApiResponse()
                .code(ApiConstants.CODE_ERROR)
                .status(ApiConstants.STATUS_ERROR)
                .message(ApiConstants.MSG_NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(IdempotencyException.class)
    public ResponseEntity<ModelApiResponse> handleIdempotency(IdempotencyException ex) {
        ModelApiResponse response = new ModelApiResponse()
                .code(ApiConstants.CODE_ERROR)
                .status(ApiConstants.STATUS_ERROR)
                .message(ApiConstants.MSG_BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ModelApiResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ModelApiResponse response = new ModelApiResponse()
                .code(ApiConstants.CODE_ERROR)
                .status(ApiConstants.STATUS_ERROR)
                .message(ApiConstants.MSG_BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ModelApiResponse> handleGeneralException(Exception ex) {
        // Here we map any unhandled error to a 500 status code, and its matching code logic.
        ModelApiResponse response = new ModelApiResponse()
                .code(ApiConstants.CODE_ERROR)
                .status(ApiConstants.STATUS_ERROR)
                .message(ApiConstants.MSG_INTERNAL_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
