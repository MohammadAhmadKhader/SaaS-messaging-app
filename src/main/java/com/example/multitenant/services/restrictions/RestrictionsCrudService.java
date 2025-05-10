package com.example.multitenant.services.restrictions;

import org.springframework.stereotype.Service;

import com.example.multitenant.models.Restriction;
import com.example.multitenant.repository.RestrictionsRepository;
import com.example.multitenant.services.helperservices.GenericCrudService;

@Service
public class RestrictionsCrudService extends GenericCrudService<Restriction, Integer>{
    private RestrictionsRepository restrictionsRepository;
    public RestrictionsCrudService(RestrictionsRepository restrictionsRepository) {
        super(restrictionsRepository);
        this.restrictionsRepository = restrictionsRepository;
    }
}