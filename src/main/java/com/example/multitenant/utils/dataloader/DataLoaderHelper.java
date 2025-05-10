package com.example.multitenant.utils.dataloader;

import java.util.List;

import org.springframework.data.domain.Example;

import com.example.multitenant.models.OrgPermission;
import com.example.multitenant.models.OrgRole;
import com.example.multitenant.models.enums.DefaultOrganizationRole;
import com.example.multitenant.repository.OrgRolesRepository;

public class DataLoaderHelper {
    static void addNewOrganizationPermissions(OrgRolesRepository repo, 
    List<OrgPermission> newPerms, DefaultOrganizationRole role) {
        var probe = new OrgRole();
        probe.setName(role.getRoleName());

        var ownerRoles = repo.findAll(Example.of(probe));

        for (OrgRole organizationRole : ownerRoles) {
           organizationRole.getOrganizationPermissions().addAll(newPerms);
           repo.save(organizationRole);
        }
    }
}
