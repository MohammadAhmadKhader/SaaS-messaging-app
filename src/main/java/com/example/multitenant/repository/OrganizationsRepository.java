package com.example.multitenant.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import com.example.multitenant.models.Organization;

import org.springframework.data.repository.query.Param;

@Repository
public interface OrganizationsRepository extends GenericRepository<Organization, Integer>, JpaSpecificationExecutor<Organization> {
    @Query("""
        SELECT o 
        FROM Organization o
        LEFT JOIN FETCH o.owner
        WHERE o.id = :organizationId
    """)
    Organization findByIdWithOwner(@Param("organizationId") Integer organizationId);
}
