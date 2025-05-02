package com.example.multitenant.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.multitenant.models.StripeSubscription;

@Repository
public interface StripeSubscriptionsRepository extends GenericRepository<StripeSubscription, UUID> {
    @Query("""
        SELECT s FROM StripeSubscription s
        WHERE s.organization.id = :organizationId
        ORDER BY s.createdAt DESC
    """)
    List<StripeSubscription> getSubsecriptionsByOrgId(@Param("organizationId") Integer organizationId);

    @Query("""
        SELECT s FROM StripeSubscription s
        WHERE s.user.id = :userId
        ORDER BY s.createdAt DESC
    """)
    List<StripeSubscription> getSubsecriptionsByUserId(@Param("userId") Integer userId);

    @Query("""
        SELECT COUNT(s) > 0 FROM StripeSubscription s
        WHERE (s.organizationId = :organizationId AND s.status = 'active' 
        AND s.currentPeriodEnd > CURRENT_TIMESTAMP)
    """)
    boolean hasValidActiveSubscription(@Param("organizationId") Integer organizationId);
} 