package com.iopexdemo.itime_backend.exceptions.custom;

public class BasicValidationException extends RuntimeException {
    public BasicValidationException(String message) {
        super(message);
    }
}
