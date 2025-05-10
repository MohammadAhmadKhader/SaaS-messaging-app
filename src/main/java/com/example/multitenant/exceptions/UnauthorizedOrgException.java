package com.example.multitenant.exceptions;

public class UnauthorizedOrgException extends RuntimeException  {
    public UnauthorizedOrgException(String message) {
        super(message);
    }
}
