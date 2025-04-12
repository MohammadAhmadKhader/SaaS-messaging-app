package com.example.multitenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.multitenant.models.GlobalRole;

import java.util.Optional;


@Repository
public interface GlobalRolesRepository extends JpaRepository<GlobalRole, Integer>, JpaSpecificationExecutor<GlobalRole> {
    public Optional<GlobalRole> findByName(String name);
}