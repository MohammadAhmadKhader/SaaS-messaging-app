package com.example.multitenant.common.resolvers.contract;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface HandleSize {
    int defaultSize() default 10;
    int minSize() default 4;
}
