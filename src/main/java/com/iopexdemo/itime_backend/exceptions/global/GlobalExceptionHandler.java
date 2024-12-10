package com.iopexdemo.itime_backend.exceptions.global;

import com.iopexdemo.itime_backend.exceptions.custom.AuthException;
import com.iopexdemo.itime_backend.exceptions.custom.BasicValidationException;
import com.iopexdemo.itime_backend.exceptions.custom.BusinessValidationException;
import com.iopexdemo.itime_backend.exceptions.custom.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> response = new HashMap<>();
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        response.put("error", errorMessage);
        response.put("status", "FAILED");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response.toString());
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<String> handleCustomException(CustomException ex)
    {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        response.put("status", "FAILED");
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(response.toString());
    }

    @ExceptionHandler(BasicValidationException.class)
    public ResponseEntity<String> handleBasicValidationException(BasicValidationException ex)
    {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        response.put("status", "FAILED");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response.toString());
    }

    @ExceptionHandler(BusinessValidationException.class)
    public ResponseEntity<String> handleBusinessValidationException(BusinessValidationException ex)
    {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        response.put("status", "FAILED");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response.toString());
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<String> handleAuthException(AuthException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }
}