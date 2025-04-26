package com.example.multitenant.common.annotations.contract;

import java.lang.annotation.*;

/**
 * @deprecated
 * will be refactored and we will use {@see PreAuthorize} instead from spring security.
 *  */ 
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AuthorizeOrg {
    String[] value();
}
