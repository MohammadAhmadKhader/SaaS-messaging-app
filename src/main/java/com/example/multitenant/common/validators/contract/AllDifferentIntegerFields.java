package com.example.multitenant.common.validators.contract;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.example.multitenant.common.validators.impl.AllDifferentIntegerFieldsValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AllDifferentIntegerFieldsValidator.class)
@Documented
public @interface AllDifferentIntegerFields {
    String message() default "fields must all be different";
    boolean shouldIgnoreNulls() default false;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String[] fieldNames();
}