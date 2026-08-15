package com.bankflow.exception;

import com.bankflow.transfers.exception.TransferInProgressException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TransferInProgressException.class)
    public ResponseEntity<ApiError> transferInProgress(TransferInProgressException ex) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new ApiError(OffsetDateTime.now(), 202, "TRANSFER_IN_PROGRESS", ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> conflict(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiError(OffsetDateTime.now(), 409, "CONFLICT", "Duplicate request"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> badRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError(OffsetDateTime.now(), 400, "BAD_REQUEST", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> validation(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError(OffsetDateTime.now(), 400, "VALIDATION_ERROR", "Invalid request body"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> internal(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError(OffsetDateTime.now(), 500, "INTERNAL_ERROR", ex.getMessage()));
    }
}