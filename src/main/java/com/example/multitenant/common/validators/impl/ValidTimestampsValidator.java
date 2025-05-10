package com.example.multitenant.common.validators.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.time.format.DateTimeFormatter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import com.example.multitenant.common.validators.contract.ValidTimestamps;

@Slf4j
public class ValidTimestampsValidator implements ConstraintValidator<ValidTimestamps, Object> {

    private boolean allowNull;
    private DateTimeFormatter formatter;
    private boolean isInstant;

    @Override
    public void initialize(ValidTimestamps constraintAnnotation) {
        this.allowNull = constraintAnnotation.allowNull();
        this.isInstant = constraintAnnotation.isInstant();
        if (!isInstant) {
            this.formatter = DateTimeFormatter.ofPattern(constraintAnnotation.pattern());
        }
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return this.allowNull;
        }

        if (isInstant) {
            return isValidInstant(value);
        } else {
            return isValidString(value);
        }
    }

      private boolean isValidString(Object value) {
        if (value instanceof String) {
            try {
                LocalDateTime.parse((String) value, formatter);
                return true;
            } catch (DateTimeParseException e) {
                log.error("an error occured during parsing string timestamp: {}", e.getMessage());
                return false;
            }
        }
        return false;
    }

    private boolean isValidInstant(Object value) {
        if (value instanceof Instant) {
            return true;
        }

        log.error("invalid instant timestamp");
        return false;
    }
}
