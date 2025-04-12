package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.demo.models.GlobalPermission;
import java.util.Optional;

@Repository
public interface GlobalPermissionsRepository extends JpaRepository<GlobalPermission, Integer>, JpaSpecificationExecutor<GlobalPermission> {
    Optional<GlobalPermission> findByName(String name);
} 
