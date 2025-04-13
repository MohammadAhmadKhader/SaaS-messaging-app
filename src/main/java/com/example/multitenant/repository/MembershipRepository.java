package com.example.multitenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.multitenant.models.Membership;
import com.example.multitenant.models.binders.MembershipKey;


@Repository
public interface MembershipRepository extends JpaRepository<Membership, MembershipKey>, JpaSpecificationExecutor<Membership> {
    
}
