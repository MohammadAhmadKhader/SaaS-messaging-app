package com.example.multitenant.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import com.example.multitenant.models.GlobalPermission;

import java.util.Optional;

@Repository
public interface GlobalPermissionsRepository extends GenericRepository<GlobalPermission, Integer> {
    Optional<GlobalPermission> findByName(String name);
} 
