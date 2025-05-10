package com.example.multitenant.common.validators.contract;

import jakarta.validation.*;
import java.lang.annotation.*;

import com.example.multitenant.common.validators.impl.AtLeastOneNotNullValidator;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AtLeastOneNotNullValidator.class)
@Documented
public @interface AtLeastOneNotNull {
    String DEFAULT_MESSAGE = "at least one of the fields must be provided";
    String message() default DEFAULT_MESSAGE;

    String[] fields();

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}