package com.example.multitenant.services.organizations;

import org.springframework.stereotype.Service;

import com.example.multitenant.models.Organization;
import com.example.multitenant.repository.OrgsRepository;
import com.example.multitenant.services.helperservices.GenericCrudService;

@Service
public class OrgsCrudService extends GenericCrudService<Organization, Integer> {
    private OrgsRepository organizationsRepository;
    public OrgsCrudService(OrgsRepository organizationsRepository) {
        super(organizationsRepository);
        this.organizationsRepository = organizationsRepository;
    }
}
