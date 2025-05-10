package com.example.multitenant.common.validators.contract;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;
import java.time.format.DateTimeFormatter;

import com.example.multitenant.common.validators.impl.ValidTimestampsValidator;

/*
 * if 'isInstant' is true which is the default, 'pattern' is ignored.
 */
@Documented
@Constraint(validatedBy = ValidTimestampsValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTimestamps {
    String message() default "invalid timestamp format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    boolean allowNull() default false;
    boolean isInstant() default true;
    String pattern() default "yyyy-MM-dd'T'HH:mm:ss"; 
}