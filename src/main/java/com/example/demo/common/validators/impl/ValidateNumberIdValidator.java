package com.example.demo.common.validators.impl;

import com.example.demo.common.validators.contract.ValidateNumberId;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidateNumberIdValidator implements ConstraintValidator<ValidateNumberId, Number> {

    private String fieldName;

    @Override
    public void initialize(ValidateNumberId constraintAnnotation) {
        this.fieldName = constraintAnnotation.name().isEmpty() ? "id" : constraintAnnotation.name();
    }

    @Override
    public boolean isValid(Number value, ConstraintValidatorContext context) {
        if (value == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(String.format("invalid %s received null", fieldName))
                    .addConstraintViolation();
            return false;
        }

        var numericValue = value.longValue();

        if (numericValue <= 0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(String.format("%s must be at least 1", fieldName))
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}