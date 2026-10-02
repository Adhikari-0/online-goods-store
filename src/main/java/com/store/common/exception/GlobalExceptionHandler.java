package com.store.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateResourceException ex) {
        
        log.warn("Duplicate resource: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)   // 409
                .body(new ErrorResponse(409, ex.getMessage()));
    }

    public record ErrorResponse(int status, String message) {}
}