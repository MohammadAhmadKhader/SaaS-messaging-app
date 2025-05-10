package com.example.multitenant.services.restrictions;

import org.springframework.stereotype.Service;

import com.example.multitenant.models.Restriction;
import com.example.multitenant.services.helperservices.ServiceSpecifications;

@Service
public class RestrictionsSpecificationsService extends ServiceSpecifications<Restriction, Integer> {
    public RestrictionsSpecificationsService() {
        super(Restriction.class);
    }
}
