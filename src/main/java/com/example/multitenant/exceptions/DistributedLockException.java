package com.example.multitenant.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DistributedLockException extends RuntimeException {
    public DistributedLockException(String message) {
        super(message);
        log.error(message);
    }

    public DistributedLockException() {
        super();
    }
}
