package com.example.multitenant.common.validators.impl;

import com.example.multitenant.common.validators.contract.*;
import jakarta.validation.*;

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
