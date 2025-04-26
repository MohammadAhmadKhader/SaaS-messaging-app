package com.example.multitenant.common.validators.impl;

import java.util.HashSet;

import com.example.multitenant.common.validators.contract.AllDifferentIntegerFields;

import jakarta.validation.*;

public class AllDifferentIntegerFieldsValidator implements ConstraintValidator<AllDifferentIntegerFields, Object> {

    private String[] fieldNames;
    private boolean shouldIgnoreNulls;

    @Override
    public void initialize(AllDifferentIntegerFields constraintAnnotation) {
        this.fieldNames = constraintAnnotation.fieldNames();
        this.shouldIgnoreNulls = constraintAnnotation.shouldIgnoreNulls();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            var seen = new HashSet<Integer>();
            for (String fieldName : fieldNames) {
                var field = value.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                var fieldValue = field.get(value);
                if (fieldValue == null) {
                    if (!this.shouldIgnoreNulls) {
                        return false;
                    }
                    
                    continue;
                }

                if (!(fieldValue instanceof Integer)) return false;

                var intValue = (Integer) fieldValue;
                if (!seen.add(intValue)) {
                    return false;
                }
            }
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
