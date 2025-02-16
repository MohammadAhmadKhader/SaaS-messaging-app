package com.example.demo.repository.organizations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.demo.models.Organization;
import com.example.demo.repository.generic.GenericRepositoryCustom;

@Repository
public interface OrganizationsRepository extends JpaRepository<Organization, Integer>, JpaSpecificationExecutor<Organization>, GenericRepositoryCustom<Organization> {

}
