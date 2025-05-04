package com.example.multitenant.common.annotations.contract;

import java.lang.annotation.*;

import com.example.multitenant.models.enums.StripeCounterOperation;
import com.example.multitenant.models.enums.StripeLimit;

/**
 * handled inside {@see SubscriptionLimitImpl.java}
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ValidateCategoryChannelsSubscriptionLimit {
    StripeLimit limit();
    StripeCounterOperation counterOperation();
    int categoryIdParamIndex();
}