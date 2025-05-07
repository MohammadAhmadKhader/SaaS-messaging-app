package com.example.multitenant.services.organizations;

import org.springframework.stereotype.Service;

import com.example.multitenant.models.Organization;
import com.example.multitenant.services.helperservices.ServiceSpecifications;

@Service
public class OrganizationsSpecificationsService extends ServiceSpecifications<Organization, Long>  {
    public OrganizationsSpecificationsService() {
        super(Organization.class);
    }
}
