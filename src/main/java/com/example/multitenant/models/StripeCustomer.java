package com.example.multitenant.models;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "stripe_customers", indexes = {
    @Index(name = "idx_stripe_customer_stripe_customer_id", columnList = "stripe_customer_id"),
    @Index(name = "idx_stripe_customer_user_id", columnList = "user_id")
})
public class StripeCustomer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @Column(name = "stripe_customer_id", nullable = false, length = 64)
    private String stripeCustomerId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "internalStripeCustomer")
    private List<StripeSubscription> subscriptions;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    public void setUserId(Long userId) {
        var user = new User();
        user.setId(userId);
        this.user = user;
    }
}