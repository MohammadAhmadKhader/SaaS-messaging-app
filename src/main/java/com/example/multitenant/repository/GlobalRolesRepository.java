package com.example.multitenant.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import com.example.multitenant.models.GlobalRole;

import java.util.Optional;


@Repository
public interface GlobalRolesRepository extends GenericRepository<GlobalRole, Integer>, JpaSpecificationExecutor<GlobalRole> {
    public Optional<GlobalRole> findByName(String name);
}