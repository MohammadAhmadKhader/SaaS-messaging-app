package com.example.demo.repository.roles;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.example.demo.models.Role;
import com.example.demo.repository.generic.GenericRepositoryCustom;
import com.example.demo.repository.generic.GenericRepositoryImpl;

public class RolesRepositoryImpl extends GenericRepositoryImpl<Role> implements GenericRepositoryCustom<Role> {

    public RolesRepositoryImpl() {
        super(Role.class);
    }

    @Override
    public Page<Role> findAllWithSpecifications(Specification<Role> spec, Pageable pageable) {
        return super.findAllWithSpecifications(spec, pageable, null);
    }

    public Page<Role> findAll(Pageable pageable) {
        return super.findAllWithSpecifications(null, pageable, null);
    }
    
}
