package com.example.multitenant.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.multitenant.models.InternalStripeCustomer;

@Repository
public interface InternalStripeCustomersRepository extends GenericRepository<InternalStripeCustomer, UUID> {
    @Query("""
        SELECT s FROM InternalStripeCustomer s
        WHERE s.user.id = :userId
    """)
    InternalStripeCustomer findCustomerByUserId(@Param("userId") Long userId);
}
