package com.example.multitenant.common.annotations.contract;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckRestricted {
    boolean isWebsocket() default false;
}