package com.example.multitenant.services.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import com.example.multitenant.exceptions.*;
import com.example.multitenant.models.*;
import com.example.multitenant.models.enums.*;
import com.example.multitenant.repository.OrgRolesRepository;
import com.example.multitenant.services.security.helperservices.OrgRolesCrudService;
import com.example.multitenant.utils.VirtualThreadsUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OrgRolesService {
    private final OrgRolesRepository rolesRepository;
    private final OrgPermissionsService orgPermissionsService;
    private final OrgRolesCrudService orgRolesCrudService;

    @Transactional
    public OrgRole createWithBasicPerms(OrgRole orgRole, Integer orgId) {
        var initialPerms = this.orgPermissionsService.findAllDefaultPermissions(DefaultOrganizationRole.ORG_USER);
        orgRole.setOrganizationPermissions(initialPerms);
        orgRole.setOrganizationId(orgId);

        return this.rolesRepository.save(orgRole);
    }

    public Page<OrgRole> findAllRoles(Integer page, Integer size, Integer organizationId) {
        var pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
        
        if (organizationId == null) {
            return this.rolesRepository.findAll(pageable);
        }

        var probe = new OrgRole();
        probe.setOrganizationId(organizationId);

        var ex = Example.of(probe);
        return this.rolesRepository.findAll(ex, pageable);
    }

    public List<OrgRole> findAllRolesWithPermissions(Integer organizationId) {
        return this.rolesRepository.findAllRolesWithPermissions(organizationId);
    }

    public OrgRole findByNameAndOrganizationId(String name, Integer orgId) {
        return this.rolesRepository.findByNameAndOrgId(name, orgId);
    }

    public OrgRole assignPermissionsToRole(Integer roleId, Set<Integer> permissionsIds, Integer organizationId) {
        var tasksResults = VirtualThreadsUtils.run(
            () -> this.rolesRepository.findByIdAndOrgIdWithPermissions(roleId, organizationId),
            () -> this.orgPermissionsService.findAllByIds(permissionsIds)
        );

        var role = tasksResults.getLeft();
        var permissions = tasksResults.getRight();

        if(role == null) {
            throw new ResourceNotFoundException("role", roleId);
        }

        if(permissions == null || permissions.size() == 0) {
            throw new InvalidOperationException(String.format("no permissions were provided to be assigned to the role"));
        }

        if(permissions.size() != permissionsIds.size()) {
            throw new InvalidOperationException("some permissions Ids are invalid");
        } 

        var currPerms = role.getOrganizationPermissions();
        var existingPermIds = currPerms.stream()
            .map((org) -> org.getId()) 
            .collect(Collectors.toSet());

        var hasAnyAlreadyAssigned = permissions.stream()
            .anyMatch(p -> existingPermIds.contains(p.getId())); 

        if (hasAnyAlreadyAssigned) {
            throw new InvalidOperationException("some or all provided permissions are already assigned to the role");
        }

        currPerms.addAll(permissions);
        this.rolesRepository.save(role);

        return role;
    }

    @Transactional
    public OrgRole unAssignPermissionsFromRole(Integer roleId, Set<Integer> permissionsIds, Integer organizationId) {
        var tasksResults = VirtualThreadsUtils.run(
            () -> this.rolesRepository.findByIdAndOrgIdWithPermissions(roleId, organizationId), 
            () -> this.orgPermissionsService.findAllByIds(permissionsIds)
        );
            
        var role = tasksResults.getLeft();
        var permissions = tasksResults.getRight();

        if(role == null) {
            throw new ResourceNotFoundException("role", roleId);
        }

        if(role.getName().equals(DefaultOrganizationRole.ORG_OWNER.getRoleName())) {
            throw new InvalidOperationException("can not remove permissions from organization owner");
        }

        if(permissions == null || permissions.size() == 0) {
            throw new InvalidOperationException("no permissions were provided to be assigned to the role");
        }

        var isRemoved = role.getOrganizationPermissions().removeIf((perm) ->permissionsIds.contains(perm.getId()));
        if(!isRemoved) {
            throw new InvalidOperationException("role does not have the permission");
        }

        return this.rolesRepository.save(role);
    }

    public void deleteRole(Integer roleId, Integer organizationId) {
        var probe = new OrgRole();
        probe.setId(roleId);
        if(organizationId != null) {
            probe.setOrganizationId(organizationId);
        }

        var ex = Example.of(probe);
        var role = this.rolesRepository.findOne(ex).orElse(null);
        if (role == null) {
            throw new ResourceNotFoundException("role", roleId);
        }

        if(DefaultOrganizationRole.isDefaultRole(role.getName())) {
            throw new InvalidOperationException("can not delete a default role");
        }

        this.rolesRepository.delete(role);
    }

    public OrgRole findOne(Integer roleId, Integer organizationId) {
        var probe = new OrgRole();
        probe.setId(roleId);
        if(organizationId != null) {
            probe.setOrganizationId(organizationId);
        }

        var ex = Example.of(probe);
        return this.rolesRepository.findOne(ex).orElse(null);
    }

    public OrgRole findOne(Integer orgRoleId) {
        return this.rolesRepository.findById(orgRoleId).orElse(null);
    }

    public OrgRole findThenUpdate(Integer orgRoleId, Integer orgId ,OrgRole orgRole) {
        var role = this.findOne(orgRoleId, orgId);
        if (role == null) {
            return null;
        }

        if(DefaultOrganizationRole.isDefaultRole(role.getName())) {
            throw new InvalidOperationException("can not update default roles names");
        }

        this.patcher(role, orgRole);

        return this.rolesRepository.save(role);
    }

    public OrgRole create(OrgRole role) {
        return this.orgRolesCrudService.create(role);
    }

    public List<OrgRole> createMany(List<OrgRole> roles) {
        return this.orgRolesCrudService.createMany(roles);
    }

    public long countOrganizationRoles(Integer orgId) {
        return this.rolesRepository.countRolesByOrgId(orgId);
    }

    // TODO: must be made by 1 request rather than 1 request per role.
    public List<OrgRole> findDefaultOrgRoles(Integer orgId) {
        var defaultOrgRoles = DefaultOrganizationRole.values();
        var orgRoleList = new ArrayList<OrgRole>();
        for (var role : defaultOrgRoles) {
            var orgRole = this.rolesRepository.findByNameAndOrgId(role.getRoleName(), orgId);
            orgRoleList.add(orgRole);
        }
        
        return orgRoleList;
    }

    private void patcher(OrgRole target, OrgRole source) {
        var newName = source.getName();
        var newDisplayName = source.getDisplayName();

        if (newName != null) {
            target.setName(newName);  
        }

        if(newDisplayName != null) {
            target.setDisplayName(newDisplayName);
        }
    }
}
