package com.example.multitenant.common.annotations.contract;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TenantHandlerLocker {
    
}
