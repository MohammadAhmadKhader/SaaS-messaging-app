package com.example.multitenant.exceptions;

public class AsyncOperationException extends RuntimeException {
    public AsyncOperationException(String message, Exception ex) {
        super(message, ex);
    }

    public AsyncOperationException(String message) {
        super(message);
    }

    public AsyncOperationException(Exception ex) {
        super(ex);
    }

}
