package com.example.multitenant.common.validators.contract;

import java.lang.annotation.*;
import com.example.multitenant.common.validators.impl.AllDifferentIntegerFieldsValidator;
import jakarta.validation.*;

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