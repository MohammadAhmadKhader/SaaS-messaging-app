package com.example.demo.repository.permissions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.demo.models.Permission;
import com.example.demo.repository.generic.GenericRepositoryCustom;
import java.util.Optional;

@Repository
public interface PermissionsRepository extends JpaRepository<Permission, Integer>, JpaSpecificationExecutor<Permission>, GenericRepositoryCustom<Permission> {
    Optional<Permission> findByName(String name);
} 
