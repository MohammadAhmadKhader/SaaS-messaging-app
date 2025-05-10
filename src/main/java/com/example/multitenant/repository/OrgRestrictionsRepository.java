package com.example.multitenant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.multitenant.models.OrgRestriction;

@Repository
public interface OrgRestrictionsRepository extends GenericRepository<OrgRestriction, Integer> {
    @Query(value = """
        SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END
        FROM organization_restrictions
        WHERE user_id = :userId AND until > CURRENT_TIMESTAMP
    """, nativeQuery = true)
    boolean isUserRestricted(@Param("userId") Long userId, @Param("organizationId") Integer organizationId);

    @Query("""
        SELECT r FROM OrgRestriction r
        LEFT JOIN FETCH r.createdBy
        LEFT JOIN FETCH r.user
        WHERE (r.id = :id AND r.organizationId = :organizationId)
    """)
    OrgRestriction findByIdAndOrgId(@Param("id") Integer id, @Param("organizationId") Integer organizationId);
}
