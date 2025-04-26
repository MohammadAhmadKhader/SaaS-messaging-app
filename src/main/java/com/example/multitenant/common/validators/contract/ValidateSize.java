package com.example.multitenant.common.validators.contract;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ValidateSize {
    int value() default 4;
}
