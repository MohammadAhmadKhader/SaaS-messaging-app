package com.example.multitenant.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.multitenant.models.OrgRole;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrgRolesRepository extends GenericRepository<OrgRole, Integer> {
    @Query("SELECT r FROM OrgRole r WHERE r.name = :name AND r.organizationId = :organizationId")
    OrgRole findByNameAndOrgId(String name, Integer organizationId);

    @Query("""
        SELECT r FROM OrgRole r
        LEFT JOIN FETCH r.organizationPermissions
        WHERE (r.id = :id AND r.organizationId = :organizationId)
    """)
    OrgRole findByIdAndOrgIdWithPermissions(@Param("id") Integer id, @Param("organizationId") Integer organizationId);

    @Query("""
        SELECT r FROM OrgRole r
        LEFT JOIN FETCH r.organizationPermissions
        WHERE (r.organizationId = :organizationId)
    """)
    List<OrgRole> findAllRolesWithPermissions(@Param("organizationId") Integer organizationId);

    @Query("SELECT COUNT(r) FROM OrgRole r WHERE r.organizationId = :organizationId")
    long countRolesByOrgId(@Param("organizationId") Integer organizationId);
}