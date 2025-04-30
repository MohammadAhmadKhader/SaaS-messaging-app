package com.example.multitenant.services.security.helperservices;

import org.springframework.stereotype.Service;

import com.example.multitenant.models.OrganizationRole;
import com.example.multitenant.repository.GlobalRolesRepository;
import com.example.multitenant.repository.OrganizationRolesRepository;
import com.example.multitenant.services.helperservices.GenericCrudService;

@Service
public class OrganizationRolesCrudService extends GenericCrudService<OrganizationRole, Integer> {
    private OrganizationRolesRepository organizationRolesRepository;
    public OrganizationRolesCrudService(OrganizationRolesRepository organizationRolesRepository) {
        super(organizationRolesRepository);
        this.organizationRolesRepository = organizationRolesRepository;
    }
}
