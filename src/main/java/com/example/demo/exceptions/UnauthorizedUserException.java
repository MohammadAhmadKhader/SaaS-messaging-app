package com.example.demo.exceptions;

import java.io.Serializable;

public class UnauthorizedUserException extends RuntimeException {
    public UnauthorizedUserException(String modelName, Serializable id) {
        super(String.format("User attempted to access resources of '%s' with id: '%s' and was deined", modelName,id));
    }

    public UnauthorizedUserException(String message) {
        super(message);
    }
}
