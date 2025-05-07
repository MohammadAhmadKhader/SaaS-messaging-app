package com.example.multitenant.exceptions;

public class UnknownException extends RuntimeException {
    public UnknownException(String message) {
        super(message);
    }

    public UnknownException(String message, Exception ex) {
        super(message, ex);
    }
}
