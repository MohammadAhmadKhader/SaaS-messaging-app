package com.example.multitenant.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import com.example.multitenant.models.Organization;

@Repository
public interface OrganizationsRepository extends GenericRepository<Organization, Integer>, JpaSpecificationExecutor<Organization> {

}
