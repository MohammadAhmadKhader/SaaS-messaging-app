package com.example.multitenant.services.security.helperservices;

import org.springframework.stereotype.Service;

import com.example.multitenant.models.GlobalRole;
import com.example.multitenant.repository.GlobalRolesRepository;
import com.example.multitenant.services.helperservices.GenericCrudService;

@Service
public class GlobalRolesCrudService extends GenericCrudService<GlobalRole, Integer> {
    private GlobalRolesRepository globalRolesRepository;
    public GlobalRolesCrudService( GlobalRolesRepository globalRolesRepository) {
        super(globalRolesRepository);
        this.globalRolesRepository = globalRolesRepository;
    }
}
