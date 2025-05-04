package com.example.multitenant.common.annotations.contract;

import java.lang.annotation.*;

import com.example.multitenant.models.enums.StripeCounterOperation;
import com.example.multitenant.models.enums.StripeLimit;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ValidateSubscriptionLimit {
    StripeLimit limit();
    StripeCounterOperation counterOperation();
}