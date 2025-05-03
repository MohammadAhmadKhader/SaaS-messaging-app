package com.example.multitenant.common.validators.contract;

import java.lang.annotation.*;

import com.example.multitenant.common.validators.impl.FileSizeValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = FileSizeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FileSize {
    String message() default "file size exceeds limit";
    long max(); // * in bytes (1 * 1024 * 1024) is 1MB
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
