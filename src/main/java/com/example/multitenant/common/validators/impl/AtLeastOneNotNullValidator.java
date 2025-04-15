package com.example.multitenant.common.validators.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.example.multitenant.common.validators.contract.AtLeastOneNotNull;

public class AtLeastOneNotNullValidator implements ConstraintValidator<AtLeastOneNotNull, Object> {

    private String[] fields;
    private String DEFAULT_MESSAGE;
    private boolean ignoreEmptyStrings;
    private String message;

    @Override
    public void initialize(AtLeastOneNotNull constraintAnnotation) {
        this.DEFAULT_MESSAGE = AtLeastOneNotNull.DEFAULT_MESSAGE;
        this.message = constraintAnnotation.message();
        this.fields = constraintAnnotation.fields();
        this.ignoreEmptyStrings = constraintAnnotation.ignoreEmptyStrings();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        if (object == null) return false;

        for (var fieldName : fields) {
            try {
                var field = object.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                var value = field.get(object);

                if (value != null) {
                    if (ignoreEmptyStrings && value instanceof String) {
                        if (!((String) value).isBlank()) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                }

            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        if(this.message.equals(this.DEFAULT_MESSAGE) || fields.length == 0) {
            var errMsg = this.DEFAULT_MESSAGE +" " +String.join(", ",fields);
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(errMsg)
                .addConstraintViolation();

        } else {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(this.message)
                .addConstraintViolation();
        }

        return false;
    }
}