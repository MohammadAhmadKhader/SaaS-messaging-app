package com.example.multitenant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.multitenant.models.OrganizationRestriction;

@Repository
public interface OrganizationRestrictionsRepository extends GenericRepository<OrganizationRestriction, Integer> {
    @Query(value = """
        SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END
        FROM organization_restrictions
        WHERE user_id = :userId AND until > CURRENT_TIMESTAMP
    """, nativeQuery = true)
    boolean isUserRestricted(@Param("userId") Long userId, @Param("organizationId") Integer organizationId);

    @Query("""
        SELECT r FROM OrganizationRestriction r
        LEFT JOIN FETCH r.createdBy
        LEFT JOIN FETCH r.user
        WHERE (r.id = :id AND r.organizationId = :organizationId)
    """)
    OrganizationRestriction findByIdAndOrgId(@Param("id") Integer id, @Param("organizationId") Integer organizationId);
}
