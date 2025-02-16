package com.example.demo.repository.organizations;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.example.demo.models.Organization;
import com.example.demo.repository.generic.GenericRepositoryCustom;
import com.example.demo.repository.generic.GenericRepositoryImpl;

public class OrganizationsRepositoryImpl extends GenericRepositoryImpl<Organization> implements GenericRepositoryCustom<Organization> {

    public OrganizationsRepositoryImpl() {
        super(Organization.class);
    }

    @Override
    public Page<Organization> findAllWithSpecifications(Specification<Organization> spec, Pageable pageable) {
        return super.findAllWithSpecifications(spec, pageable, null);
    }

    public Page<Organization> findAll(Pageable pageable) {
        return super.findAllWithSpecifications(null, pageable, null);
    }
    
}
