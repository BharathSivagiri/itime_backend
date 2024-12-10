package com.iopexdemo.itime_backend.exceptions.custom;

public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}
