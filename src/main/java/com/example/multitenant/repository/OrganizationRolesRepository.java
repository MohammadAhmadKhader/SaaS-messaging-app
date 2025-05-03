package com.example.multitenant.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.multitenant.models.OrganizationRole;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationRolesRepository extends GenericRepository<OrganizationRole, Integer> {
    public Optional<OrganizationRole> findByNameAndOrganizationId(String name, Integer organizationId);

    @Query("""
        SELECT r FROM OrganizationRole r
        LEFT JOIN FETCH r.organizationPermissions
        WHERE (r.id = :id AND r.organizationId = :organizationId)
    """)
    OrganizationRole findByIdAndOrgIdWithPermissions(@Param("id") Integer id, @Param("organizationId") Integer organizationId);

    @Query("""
        SELECT r FROM OrganizationRole r
        LEFT JOIN FETCH r.organizationPermissions
        WHERE (r.organizationId = :organizationId)
    """)
    List<OrganizationRole> findAllRolesWithPermissions(@Param("organizationId") Integer organizationId);

    @Query("SELECT COUNT(r) FROM OrganizationRole r WHERE r.organizationId = :organizationId")
    long countRolesByOrganizationId(@Param("organizationId") Integer organizationId);
}