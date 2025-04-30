package com.example.multitenant.utils.dataloader;

import java.util.List;

import org.springframework.data.domain.Example;

import com.example.multitenant.models.OrganizationPermission;
import com.example.multitenant.models.OrganizationRole;
import com.example.multitenant.models.enums.DefaultOrganizationRole;
import com.example.multitenant.repository.OrganizationRolesRepository;

public class DataLoaderHelper {
    static void addNewOrganizationPermissions(OrganizationRolesRepository repo, 
    List<OrganizationPermission> newPerms, DefaultOrganizationRole role) {
        var probe = new OrganizationRole();
        probe.setName(role.getRoleName());

        var ownerRoles = repo.findAll(Example.of(probe));

        for (OrganizationRole organizationRole : ownerRoles) {
           organizationRole.getOrganizationPermissions().addAll(newPerms);
           repo.save(organizationRole);
        }
    }
}
