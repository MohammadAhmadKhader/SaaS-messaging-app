package com.example.multitenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.multitenant.models.OrganizationMembership;
import com.example.multitenant.models.binders.OrganizationMembershipKey;

@Repository
public interface OrganizationMembershipRepository extends JpaRepository<OrganizationMembership, OrganizationMembershipKey>, JpaSpecificationExecutor<OrganizationMembership> {
    
}
