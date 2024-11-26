package com.iopexdemo.itime_backend.exceptions.global;

import com.iopexdemo.itime_backend.exceptions.custom.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<String> handleCustomException(CustomException ex)
    {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        response.put("status", "FAILED");
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(response.toString());
    }
}