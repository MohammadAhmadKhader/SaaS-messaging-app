package com.example.multitenant.exceptions;

import java.io.Serializable;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourceName, Serializable resourceId) {
        super(String.format("%s with id: '%s' was not found", resourceName, resourceId.toString()));
    }

    public ResourceNotFoundException(String resourceName) {
        super(String.format("%s was not found", resourceName));
    }
}
