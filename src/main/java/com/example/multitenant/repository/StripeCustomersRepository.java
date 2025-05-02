package com.example.multitenant.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.multitenant.models.StripeCustomer;

@Repository
public interface StripeCustomersRepository extends GenericRepository<StripeCustomer, UUID> {
    @Query("""
        SELECT s FROM StripeCustomer s
        WHERE s.user.id = :userId
    """)
    StripeCustomer findCustomerByUserId(@Param("userId") Long userId);
}
