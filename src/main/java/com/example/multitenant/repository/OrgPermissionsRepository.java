package com.example.multitenant.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import com.example.multitenant.models.OrgPermission;

import java.util.Optional;

@Repository
public interface OrgPermissionsRepository extends GenericRepository<OrgPermission, Integer> {
    Optional<OrgPermission> findByName(String name);
} 
