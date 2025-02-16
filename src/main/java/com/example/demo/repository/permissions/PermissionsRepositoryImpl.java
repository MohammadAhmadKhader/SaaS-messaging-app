package com.example.demo.repository.permissions;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.example.demo.models.Organization;
import com.example.demo.models.Permission;
import com.example.demo.models.Role;
import com.example.demo.repository.generic.GenericRepositoryCustom;
import com.example.demo.repository.generic.GenericRepositoryImpl;

public class PermissionsRepositoryImpl extends GenericRepositoryImpl<Permission> implements GenericRepositoryCustom<Permission> {
    
    public PermissionsRepositoryImpl() {
        super(Permission.class);
    }
    
    @Override
    public Page<Permission> findAllWithSpecifications(Specification<Permission> spec, Pageable pageable) {
        return super.findAllWithSpecifications(spec, pageable, null);
    }

    public Page<Permission> findAll(Pageable pageable) {
        return super.findAllWithSpecifications(null, pageable, null);
    }
}
