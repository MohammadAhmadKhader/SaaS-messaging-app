package com.example.multitenant.services.organizations;

import org.springframework.stereotype.Service;

import com.example.multitenant.models.Organization;
import com.example.multitenant.repository.OrganizationsRepository;
import com.example.multitenant.services.helperservices.GenericCrudService;

@Service
public class OrganizationsCrudService extends GenericCrudService<Organization, Integer> {
    private OrganizationsRepository organizationsRepository;
    public OrganizationsCrudService(OrganizationsRepository organizationsRepository) {
        super(organizationsRepository);
        this.organizationsRepository = organizationsRepository;
    }
}
