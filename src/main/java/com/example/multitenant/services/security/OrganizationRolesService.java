package com.example.multitenant.services.security;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.multitenant.exceptions.AsyncOperationException;
import com.example.multitenant.exceptions.InvalidOperationException;
import com.example.multitenant.exceptions.ResourceNotFoundException;
import com.example.multitenant.exceptions.UnknownException;
import com.example.multitenant.models.OrganizationRole;
import com.example.multitenant.models.enums.DefaultOrganizationRole;
import com.example.multitenant.repository.OrganizationRolesRepository;
import com.example.multitenant.services.generic.GenericService;

import jakarta.transaction.Transactional;

@Service
public class OrganizationRolesService extends GenericService<OrganizationRole, Integer> {
    private final OrganizationRolesRepository rolesRepository;
    private final OrganizationPermissionsService organizationPermissionsService;

    public OrganizationRolesService(OrganizationRolesRepository rolesRepository, OrganizationPermissionsService organizationPermissionsService) {
        super(rolesRepository);
        this.rolesRepository = rolesRepository;
        this.organizationPermissionsService = organizationPermissionsService;
    }

    @Transactional
    public OrganizationRole createWithBasicPerms(OrganizationRole orgRole, Integer orgId) {
        var initialPerms = this.organizationPermissionsService.findAllDefaultPermissions(DefaultOrganizationRole.ORG_USER);
        orgRole.setOrganizationPermissions(initialPerms);
        orgRole.setOrganizationId(orgId);

        return this.rolesRepository.save(orgRole);
    }

    public Page<OrganizationRole> findAllRoles(Integer page, Integer size, Integer organizationId) {
        var pageable = PageRequest.of(page - 1, size);
        
        if (organizationId == null) {
            return this.rolesRepository.findAll(pageable);
        }

        var probe = new OrganizationRole();
        probe.setOrganizationId(organizationId);

        var ex = Example.of(probe);
        return this.rolesRepository.findAll(ex, pageable);
    }

    public OrganizationRole findByNameAndOrganizationId(String name, Integer orgId) {
        return this.rolesRepository.findByNameAndOrganizationId(name, orgId).orElse(null);
    }

    public OrganizationRole assignPermissionsToRole(Integer roleId, Set<Integer> permissionsIds, Integer organizationId) {
        try {
            var roleTask = CompletableFuture.supplyAsync(() -> this.rolesRepository.findByIdAndOrgIdWithPermissions(roleId, organizationId));
            var permissionsTask = CompletableFuture.supplyAsync(() -> this.organizationPermissionsService.findAllByIds(permissionsIds));
            
            var role = roleTask.get();
            var permissions = permissionsTask.get();

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
        } catch (InterruptedException | ExecutionException ex) {
            throw new AsyncOperationException("Error occurred during task execution", ex);
        }
    }

    @Transactional
    public OrganizationRole unAssignPermissionsFromRole(Integer roleId, Set<Integer> permissionsIds, Integer organizationId) {
        try {
            var roleTask = CompletableFuture.supplyAsync(() -> this.rolesRepository.findByIdAndOrgIdWithPermissions(roleId, organizationId));
            var permissionsTask = CompletableFuture.supplyAsync(() -> this.organizationPermissionsService.findAllByIds(permissionsIds));
            
            var role = roleTask.get();
            var permissions = permissionsTask.get();

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
                throw new UnknownException("an error has occured during attempt to remove permissions from organization role");
            }

            return this.rolesRepository.save(role);
        } catch (InterruptedException | ExecutionException ex) {
            throw new AsyncOperationException("Error occurred during task execution", ex);
        }
    }

    public void deleteRole(Integer roleId, Integer organizationId) {
        var probe = new OrganizationRole();
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

    public OrganizationRole findOne(Integer roleId, Integer organizationId) {
        var probe = new OrganizationRole();
        probe.setId(roleId);
        if(organizationId != null) {
            probe.setOrganizationId(organizationId);
        }

        var ex = Example.of(probe);
        return this.rolesRepository.findOne(ex).orElse(null);
    }

    public OrganizationRole findOne(Integer orgRoleId) {
        return this.rolesRepository.findById(orgRoleId).orElse(null);
    }

    public OrganizationRole findThenUpdate(Integer orgRoleId, Integer orgId ,OrganizationRole orgRole) {
        var role = this.findOne(orgRoleId, orgId);
        if (role == null) {
            return null;
        }

        this.patcher(role, orgRole);

        return this.save(role);
    }

    private void patcher(OrganizationRole target, OrganizationRole source) {
        var newName = source.getName();

        if (newName != null) {
            target.setName(newName);
        }
    }
}
