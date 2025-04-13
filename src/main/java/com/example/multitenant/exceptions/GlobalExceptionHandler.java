package com.example.multitenant.exceptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.example.multitenant.dtos.apiResponse.ApiResponses;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        var errors = new ArrayList<String>();
        var resp = new HashMap<String, Object>();

        var violations = ex.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            String fieldName = violation.getPropertyPath().toString();
            fieldName = fieldName.substring(fieldName.lastIndexOf('.') + 1);

            var errorMessage = violation.getMessage();
            errors.add(errorMessage);
        }

        resp.put("errors", errors);
        return ResponseEntity.badRequest().body(resp);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        var paramName = ex.getName();
        var invalidValue = String.valueOf(ex.getValue());
        logger.error("[MethodArgumentTypeMismatchException]: " +ex.getMessage());

        var errRes = ApiResponses.GetErrIdIsRequired(paramName, invalidValue);
        return ResponseEntity.badRequest().body(errRes);
    }


    @ExceptionHandler(UnauthorizedUserException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorizedExceptions(UnauthorizedUserException ex) {
        var errBody = ApiResponses.Unauthorized();
        logger.error("[User Unauthorized]: " +ex.getMessage());

        return ResponseEntity.internalServerError().body(errBody);
    }

    @ExceptionHandler(UnauthorizedOrganizationException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorizedExceptions(UnauthorizedOrganizationException ex) {
        var errBody = ApiResponses.Unauthorized();
        logger.error("[Organization Unauthorized]: " +ex.getMessage());

        return ResponseEntity.internalServerError().body(errBody);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleReesouceNotFound(ResourceNotFoundException ex) {
        var errBody = ApiResponses.GetErrResponse(ex);
        return ResponseEntity.badRequest().body(errBody);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAuthorizationDenied(AuthorizationDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponses.Forbidden());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponses.InvalidEmailOrPassword());
    }
    
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Map<String, Object>> handleUnknownErrors(Throwable ex) {
        var errBody = ApiResponses.GetInternalErr();
        logger.error("cause:{} - message:{}",ex.getCause(),ex.getMessage());
        ex.printStackTrace();
        
        return ResponseEntity.internalServerError().body(errBody);
    }
}