package com.example.multitenant.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.multitenant.models.InternalStripeSubscription;

@Repository
public interface InternalStripeSubscriptionsRepository extends GenericRepository<InternalStripeSubscription, UUID> {
    @Query("""
        SELECT s FROM InternalStripeSubscription s
        WHERE s.organization.id = :organizationId
        ORDER BY s.createdAt DESC
    """)
    List<InternalStripeSubscription> findByOrgId(@Param("organizationId") Integer organizationId);

    @Query("""
        SELECT s FROM InternalStripeSubscription s
        WHERE s.user.id = :userId
        ORDER BY s.createdAt DESC
    """)
    List<InternalStripeSubscription> findByUserId(@Param("userId") Integer userId);

    @Query("""
        SELECT s FROM InternalStripeSubscription s
        WHERE s.stripeSubscriptionId = :stripeSubscriptionId
    """)
    InternalStripeSubscription findByStripeId(@Param("stripeSubscriptionId") String stripeSubscriptionId);

    @Query("""
        SELECT COUNT(s) > 0 FROM InternalStripeSubscription s
        WHERE (s.organizationId = :organizationId AND s.status = 'active' 
        AND s.currentPeriodEnd > CURRENT_TIMESTAMP)
    """)
    boolean hasActive(@Param("organizationId") Integer organizationId);

    @Query("""
        SELECT s FROM InternalStripeSubscription s
        WHERE s.organization.id = :organizationId AND s.status = 'active'
    """)
    InternalStripeSubscription findActiveByOrgId(@Param("organizationId") Integer organizationId);
} 