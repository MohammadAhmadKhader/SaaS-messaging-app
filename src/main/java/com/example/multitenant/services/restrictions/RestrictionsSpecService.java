package com.example.multitenant.services.restrictions;

import org.springframework.stereotype.Service;

import com.example.multitenant.models.Restriction;
import com.example.multitenant.services.helperservices.SpecificationsService;

@Service
public class RestrictionsSpecService extends SpecificationsService<Restriction, Integer> {
    public RestrictionsSpecService() {
        super(Restriction.class);
    }
}
