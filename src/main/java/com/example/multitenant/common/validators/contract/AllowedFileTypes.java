package com.example.multitenant.common.validators.contract;

import java.lang.annotation.*;

import com.example.multitenant.common.validators.impl.FileTypeValidator;

import jakarta.validation.*;

@Documented
@Constraint(validatedBy = FileTypeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AllowedFileTypes {
    String message() default "invalid file type";
    String[] types();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}