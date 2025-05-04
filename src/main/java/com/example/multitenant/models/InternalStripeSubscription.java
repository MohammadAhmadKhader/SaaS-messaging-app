package com.example.multitenant.models;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "internal_stripe_subscriptions", indexes = {
    @Index(name = "idx_internal_stripe_sub_stripe_subscription_id", columnList = "stripe_subscription_id"),
    @Index(name = "idx_internal_stripe_sub_organization_id", columnList = "organization_id"),
    @Index(name = "idx_internal_stripe_sub_user_id", columnList = "status"),
    @Index(name = "idx_internal_status", columnList = "user_id")
})
public class InternalStripeSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private Integer organizationId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", updatable = false, insertable = false)
    private Organization organization;

    @Column(name = "stripe_subscription_id", nullable = false, unique = true, length = 64)
    private String stripeSubscriptionId;

    @Column(name = "stripe_price_id", nullable = false, length = 64)
    private String stripePriceId;

    @Column(name = "stripe_customer_id", nullable = false, length = 64)
    private String stripeCustomerId;

    @Column(name = "internal_customer_id", nullable = false, length = 64)
    private UUID internalCustomerId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "internal_customer_id", updatable = false, insertable = false, nullable = false)
    private InternalStripeCustomer internalStripeCustomer;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", updatable = false, insertable = false)
    private User user;

    @Column(name = "status", nullable = false, length = 64)
    private String status;

    @Column(name = "tier", nullable = false, length = 34)
    private String tier;

    @Column(name = "current_period_end")
    private Instant currentPeriodEnd;

    @Column(name = "current_period_start")
    private Instant currentPeriodStart;

    @Column(name = "current_period_at")
    private Instant cancelPeriodAt;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;
}