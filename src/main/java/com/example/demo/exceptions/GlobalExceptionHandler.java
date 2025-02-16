package com.example.demo.exceptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.demo.dtos.apiResponse.ApiResponses;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        var errors = new ArrayList<String>();
        var resp = new HashMap<String, Object>();

        ex.getFieldErrors().forEach((e) -> {
            errors.add(e.getField()+ ": " +e.getDefaultMessage());
        });

        resp.put("errors", errors);

        return ResponseEntity.badRequest().body(resp);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> handleUnauthorizedExceptions(UnauthorizedUserException ex) {
        var errBody = ApiResponses.Unauthorized();
        logger.error("[User Unauthorized]: " +ex.getMessage());

        return ResponseEntity.internalServerError().body(errBody);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> handleUnauthorizedExceptions(UnauthorizedOrganizationException ex) {
        var errBody = ApiResponses.Unauthorized();
        logger.error("[Organization Unauthorized]: " +ex.getMessage());

        return ResponseEntity.internalServerError().body(errBody);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> handleReesouceNotFound(ResourceNotFoundException ex) {
        var errBody = ApiResponses.GetErrResponse(ex);
        return ResponseEntity.badRequest().body(errBody);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> handleUnknownErrors(Exception ex) {
        var errBody = ApiResponses.GetErrResponse(ex);
        logger.error("cause:{} - message:{}",ex.getCause(),ex.getMessage());
        
        return ResponseEntity.internalServerError().body(errBody);
    }
}