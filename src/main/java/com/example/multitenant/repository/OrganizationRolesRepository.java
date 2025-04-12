package com.example.multitenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.multitenant.models.OrganizationRole;

import java.util.Optional;


@Repository
public interface OrganizationRolesRepository extends JpaRepository<OrganizationRole, Integer>, JpaSpecificationExecutor<OrganizationRole> {
    public Optional<OrganizationRole> findByName(String name);
}