package com.example.multitenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.multitenant.models.OrganizationPermission;

import java.util.Optional;

@Repository
public interface OrganizationPermissionsRepository extends GenericRepository<OrganizationPermission, Integer>, JpaSpecificationExecutor<OrganizationPermission> {
    Optional<OrganizationPermission> findByName(String name);
} 
