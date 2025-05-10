package com.example.multitenant.services.organizationsrestrictions;

import org.springframework.stereotype.Service;

import com.example.multitenant.models.OrganizationRestriction;
import com.example.multitenant.services.helperservices.ServiceSpecifications;

@Service
public class OrganizationRestrictionsSpecificationsService extends ServiceSpecifications<OrganizationRestriction, Integer> {
    public OrganizationRestrictionsSpecificationsService() {
        super(OrganizationRestriction.class);
    }
}
