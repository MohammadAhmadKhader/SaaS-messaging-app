package com.example.multitenant.exceptions;

public class UnauthorizedOrganizationException extends RuntimeException  {
    public UnauthorizedOrganizationException(String message) {
        super(message);
    }
}
