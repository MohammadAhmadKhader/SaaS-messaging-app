package com.example.multitenant.common.annotations.contract;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @deprecated
 * will be refactored and we will use {@see PreAuthorize} instead from spring security.
 *  */ 
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AuthorizeOrg {
    String[] value();
}
