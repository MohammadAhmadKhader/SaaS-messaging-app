package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.demo.models.OrganizationMembership;
import com.example.demo.models.binders.OrganizationMembershipKey;

@Repository
public interface OrganizationMembershipRepository extends JpaRepository<OrganizationMembership, OrganizationMembershipKey>, JpaSpecificationExecutor<OrganizationMembership> {
    
}
