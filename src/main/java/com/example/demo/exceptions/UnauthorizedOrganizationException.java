package com.example.demo.exceptions;

public class UnauthorizedOrganizationException extends RuntimeException  {
    public UnauthorizedOrganizationException(String message) {
        super(message);
    }
}
