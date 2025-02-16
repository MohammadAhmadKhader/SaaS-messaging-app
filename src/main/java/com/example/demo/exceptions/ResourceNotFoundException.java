package com.example.demo.exceptions;

import java.io.Serializable;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourceName, Serializable resourceId) {
        super(String.format("%s with id: '%s' was not found", resourceName));
    }
}
