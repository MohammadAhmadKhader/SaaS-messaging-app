package com.example.demo.repository.roles;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.demo.models.Organization;
import com.example.demo.models.Role;
import com.example.demo.repository.generic.GenericRepositoryCustom;

import java.lang.classfile.ClassFile.Option;
import java.util.List;
import java.util.Optional;


@Repository
public interface RolesRepository extends JpaRepository<Role, Integer>, JpaSpecificationExecutor<Role>, GenericRepositoryCustom<Role> {
    public Optional<Role> findByName(String name);
}