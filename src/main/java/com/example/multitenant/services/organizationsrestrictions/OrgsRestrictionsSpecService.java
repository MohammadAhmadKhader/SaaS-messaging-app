package com.example.multitenant.services.organizationsrestrictions;

import org.springframework.stereotype.Service;

import com.example.multitenant.models.OrgRestriction;
import com.example.multitenant.services.helperservices.SpecificationsService;

@Service
public class OrgsRestrictionsSpecService extends SpecificationsService<OrgRestriction, Integer> {
    public OrgsRestrictionsSpecService() {
        super(OrgRestriction.class);
    }
}
