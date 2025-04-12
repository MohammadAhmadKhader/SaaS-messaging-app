package com.example.demo.common.validators.contract;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.example.demo.common.validators.impl.ValidateNumberIdValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = ValidateNumberIdValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateNumberId {
    String message() default "Invalid Id";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String name() default "";
}