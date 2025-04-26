package com.example.multitenant.common.validators.contract;

import java.lang.annotation.*;
import com.example.multitenant.common.validators.impl.ValidateNumberIdValidator;
import jakarta.validation.*;

@Constraint(validatedBy = ValidateNumberIdValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateNumberId {
    String message() default "Invalid Id";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String name() default "";
}