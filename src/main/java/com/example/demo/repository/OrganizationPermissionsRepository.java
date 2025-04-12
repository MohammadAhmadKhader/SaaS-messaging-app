package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.demo.models.OrganizationPermission;
import java.util.Optional;

@Repository
public interface OrganizationPermissionsRepository extends JpaRepository<OrganizationPermission, Integer>, JpaSpecificationExecutor<OrganizationPermission> {
    Optional<OrganizationPermission> findByName(String name);
} 
