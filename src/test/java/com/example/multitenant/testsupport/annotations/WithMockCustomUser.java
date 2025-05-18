package com.example.multitenant.testsupport.annotations;

import java.lang.annotation.*;

import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {
    String username() default "user";
    String[] roles() default {};
    String[] authorities() default {};
}