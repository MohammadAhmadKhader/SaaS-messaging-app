package com.example.multitenant.common.validators.impl;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.example.multitenant.common.validators.contract.AtLeastInFuture;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AtLeastInFutureValidator implements ConstraintValidator<AtLeastInFuture, Instant> {
    private int amount;
    private ChronoUnit unit;

    @Override
    public void initialize(AtLeastInFuture constraintAnnotation) {
        this.amount = constraintAnnotation.amount();
        this.unit = constraintAnnotation.unit();
    }

    @Override
    public boolean isValid(Instant value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        var minValid = Instant.now().plus(amount, unit);
        return value.isAfter(minValid);
    }
}