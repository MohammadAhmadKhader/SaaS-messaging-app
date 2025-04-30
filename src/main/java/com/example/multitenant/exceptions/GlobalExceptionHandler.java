package com.example.multitenant.exceptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.example.multitenant.dtos.apiresponse.ApiResponses;

import jakarta.validation.*;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException ex) {
        var errors = new ArrayList<String>();
        
        var nonFieldError = ex.getBindingResult().getAllErrors().get(0);
        errors.add(nonFieldError.getDefaultMessage());
        var body = ApiResponses.OneKey("errors", errors);

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<Object> handleInvalidOperationException(InvalidOperationException ex) {
        var errors = new ArrayList<String>();
        errors.add(ex.getMessage());
        var body = ApiResponses.OneKey("errors", errors);
        
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        var errors = new ArrayList<String>();
        var errMsg = "the request body is missing or malformed";
        errors.add(errMsg);
        var body = ApiResponses.OneKey("errors", errors);

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleValidationException(BadRequestException ex) {
        var errors = new ArrayList<String>();
        errors.add(ex.getMessage());
        var body = ApiResponses.OneKey("errors", errors);

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(UnauthorizedUserException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorizedExceptions(UnauthorizedUserException ex) {
        var errBody = ApiResponses.Unauthorized();
        logger.error("[User Unauthorized]: " +ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errBody);
    }

    @ExceptionHandler(UnauthorizedOrganizationException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorizedExceptions(UnauthorizedOrganizationException ex) {
        var errBody = ApiResponses.Unauthorized();
        logger.error("[Organization Unauthorized]: " +ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errBody);
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

    @ExceptionHandler(UnknownException.class)
    public ResponseEntity<Map<String, Object>> handleUnknownException(UnknownException ex) {
        return ResponseEntity.internalServerError().body(ApiResponses.GetInternalErr(ex.getMessage()));
    }

    @ExceptionHandler(AsyncOperationException.class)
    public ResponseEntity<Map<String, Object>> handleAsyncOperationException(AsyncOperationException ex) {
        return ResponseEntity.internalServerError().body(ApiResponses.GetInternalErr(ex.getMessage()));
    }
    
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Map<String, Object>> handleUnknownErrors(Throwable ex) {
        var errBody = ApiResponses.GetInternalErr();
        logger.error("cause:{} - message:{}",ex.getCause(),ex.getMessage());
        ex.printStackTrace();
        
        return ResponseEntity.internalServerError().body(errBody);
    }
}