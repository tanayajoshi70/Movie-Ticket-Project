package com.cdac.custom_exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        ErrorResponse err = new ErrorResponse();
        err.setTimestamp(LocalDateTime.now());
        err.setStatus(HttpStatus.BAD_REQUEST.value());
        err.setError("Bad Request");
        err.setMessage(ex.getMessage());

        return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
    }
}
