package com.example.multitenant.common.validators.contract;

import java.lang.annotation.*;
import java.time.temporal.ChronoUnit;

import com.example.multitenant.common.validators.impl.AtLeastInFutureValidator;

import jakarta.validation.*;

/*
 * null assumed to be valid
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AtLeastInFutureValidator.class)
public @interface AtLeastInFuture {
    String message() default "date must be at least {amount} {unit} in the future";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    int amount();
    ChronoUnit unit();
}
