package com.example.multitenant.dtos.stripe;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import com.example.multitenant.models.InternalStripeCustomer;
import com.example.multitenant.models.InternalStripeSubscription;
import com.example.multitenant.models.Organization;

import jakarta.persistence.*;
import lombok.*;
@Getter
@Setter
@NoArgsConstructor
public class InternalStripeSubscriptionCacheDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Integer organizationId;
    private Organization organization;
    private String stripeSubscriptionId;
    private String stripePriceId;
    private String stripeCustomerId;
    private UUID internalCustomerId;
    private Long userId;
    private String status;
    // stripe product name
    private String tier;
    private Instant currentPeriodEnd;
    private Instant currentPeriodStart;
    private Instant cancelPeriodAt;
    private Instant createdAt;

    public InternalStripeSubscriptionCacheDTO(InternalStripeSubscription internalStripeSubscription) {
        this.setId(internalStripeSubscription.getId());
        this.setOrganizationId(internalStripeSubscription.getOrganizationId());
        this.setOrganization(internalStripeSubscription.getOrganization());
        this.setStripeSubscriptionId(internalStripeSubscription.getStripeSubscriptionId());
        this.setStripePriceId(internalStripeSubscription.getStripePriceId());
        this.setStripeCustomerId(internalStripeSubscription.getStripeCustomerId());
        this.setInternalCustomerId(internalStripeSubscription.getInternalCustomerId());
        this.setUserId(internalStripeSubscription.getUserId());
        this.setStatus(internalStripeSubscription.getStatus());
        this.setTier(internalStripeSubscription.getStripeProductName());
        this.setCurrentPeriodEnd(internalStripeSubscription.getCurrentPeriodEnd());
        this.setCurrentPeriodStart(internalStripeSubscription.getCurrentPeriodStart());
        this.setCancelPeriodAt(internalStripeSubscription.getCancelPeriodAt());
        this.setCreatedAt(internalStripeSubscription.getCreatedAt());
    }
}