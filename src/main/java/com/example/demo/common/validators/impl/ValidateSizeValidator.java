package com.example.demo.common.validators.impl;

import com.example.demo.common.validators.contract.ValidateNumberId;
import com.example.demo.common.validators.contract.ValidateSize;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidateSizeValidator implements ConstraintValidator<ValidateSize, Integer> {
    
    private int minValue = 4;

    @Override
    public void initialize(ValidateSize constraintAnnotation) {
        this.minValue = constraintAnnotation.value();
    }
    
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value < this.minValue) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(String.format("size must be at least %s", this.minValue))
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
    
}
