package com.example.multitenant.services.security.helperservices;

import org.springframework.stereotype.Service;

import com.example.multitenant.models.OrgRole;
import com.example.multitenant.repository.GlobalRolesRepository;
import com.example.multitenant.repository.OrgRolesRepository;
import com.example.multitenant.services.helperservices.GenericCrudService;

@Service
public class OrgRolesCrudService extends GenericCrudService<OrgRole, Integer> {
    private OrgRolesRepository orgRolesRepository;
    public OrgRolesCrudService(OrgRolesRepository orgRolesRepository) {
        super(orgRolesRepository);
        this.orgRolesRepository = orgRolesRepository;
    }
}
