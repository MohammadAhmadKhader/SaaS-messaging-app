package com.example.multitenant.models.enums;

public enum StripeEventType {
    // we may need those we may not need all, we will check lately.
    CHECKOUT_SESSION_COMPLETED("checkout.session.completed"),
    CUSTOMER_SUBSCRIPTION_CREATED("customer.subscription.created"),
    CUSTOMER_SUBSCRIPTION_UPDATED("customer.subscription.updated"),
    CUSTOMER_SUBSCRIPTION_DELETED("customer.subscription.deleted"),
    CUSTOMER_SUBSCRIPTION_PAUSED("customer.subscription.paused"),
    CUSTOMER_SUBSCRIPTION_RESUMED("customer.subscription.resumed"),
    CUSTOMER_SUBSCRIPTION_PENDING_UPDATE_APPLIED("customer.subscription.pending_update_applied"),
    CUSTOMER_SUBSCRIPTION_PENDING_UPDATE_EXPIRED("customer.subscription.pending_update_expired"),

    INVOICE_PAID("invoice.paid"),
    INVOICE_PAYMENT_FAILED("invoice.payment_failed"),
    INVOICE_PAYMENT_ACTION_REQUIRED("invoice.payment_action_required"),
    INVOICE_UPCOMING("invoice.upcoming"),
    INVOICE_MARKED_UNCOLLECTIBLE("invoice.marked_uncollectible"),
    INVOICE_PAYMENT_SUCCEEDED("invoice.payment_succeeded"),
    PAYMENT_INTENT_SUCCEEDED("payment_intent.succeeded"),
    PAYMENT_INTENT_PAYMENT_FAILED("payment_intent.payment_failed"),
    PAYMENT_INTENT_CANCELED("payment_intent.canceled");

    private final String event;

    StripeEventType(String event) {
        this.event = event;
    }

    public String getEvent() {
        return event;
    }

    @Override
    public String toString() {
        return this.event;
    }
}
