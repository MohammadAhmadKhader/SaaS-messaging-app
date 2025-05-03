package com.example.multitenant.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AppFilesException extends RuntimeException {
    public AppFilesException(String message, int statusCode, String responseBody) {
        super(message);
        log.error("[Files Exception] message: {}, status-code: {}, response-body: {}", message, statusCode, responseBody);
    }

    public AppFilesException(String message, Exception ex) {
        super(message, ex);
        log.error("[Files Exception] message: {}, exception-message: {}, cause: {}", message, ex.getMessage(), ex.getCause());
    }
}
