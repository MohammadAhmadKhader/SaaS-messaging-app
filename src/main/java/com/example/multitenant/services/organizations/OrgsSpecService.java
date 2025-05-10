package com.example.multitenant.services.organizations;

import org.springframework.stereotype.Service;

import com.example.multitenant.models.Organization;
import com.example.multitenant.services.helperservices.SpecificationsService;

@Service
public class OrgsSpecService extends SpecificationsService<Organization, Long>  {
    public OrgsSpecService() {
        super(Organization.class);
    }
}
