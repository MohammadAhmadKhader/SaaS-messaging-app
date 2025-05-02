package com.example.multitenant.exceptions;

import com.example.multitenant.utils.ConsoleColorUtils;
import com.stripe.exception.StripeException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AppStripeException extends RuntimeException {
    public AppStripeException(String errMsg) {
        super(errMsg);
        log.error(ConsoleColorUtils.red("[Stripe Exception] message: {}"), errMsg);
    }

    public AppStripeException(String errMsg, StripeException ex) {
        super(errMsg);
        log.error(ConsoleColorUtils.red("[Stripe Exception] message: {}"), ex.getMessage());
    }
}
