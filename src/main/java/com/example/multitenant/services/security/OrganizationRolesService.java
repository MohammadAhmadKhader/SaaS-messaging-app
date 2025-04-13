package com.example.multitenant.services.security;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.multitenant.exceptions.ResourceNotFoundException;
import com.example.multitenant.models.OrganizationRole;
import com.example.multitenant.repository.OrganizationRolesRepository;
import com.example.multitenant.services.generic.GenericService;

@Service
public class OrganizationRolesService extends GenericService<OrganizationRole, Integer> {
    private final OrganizationRolesRepository rolesRepository;
    private final OrganizationPermissionsService organizationPermissionsService;

    public OrganizationRolesService(OrganizationRolesRepository rolesRepository, OrganizationPermissionsService organizationPermissionsService) {
        super(rolesRepository);
        this.rolesRepository = rolesRepository;
        this.organizationPermissionsService = organizationPermissionsService;
    }

    public Page<OrganizationRole> findAllRoles(Integer page, Integer size, Integer organizationId) {
        var pageable = PageRequest.of(page - 1, size);
        
        Page<OrganizationRole> result; 
        if (organizationId == null) {
            return this.rolesRepository.findAll(pageable);
        }

        var probe = new OrganizationRole();
        probe.setOrganizationId(organizationId);

        var ex = Example.of(probe);
        return this.rolesRepository.findAll(ex, pageable);
    }

    public OrganizationRole findByName(String name) {
        return this.rolesRepository.findByName(name).orElse(null);
    }

    public OrganizationRole assignPermissionsToRole(Integer roleId, Set<Integer> permissionsIds, Integer organizationId) {
        try {
            var roleTask = CompletableFuture.supplyAsync(() -> {
                var probe = new OrganizationRole();
                if(organizationId != null) {
                    probe.setOrganizationId(organizationId);
                }
                
                probe.setId(roleId);
                var ex = Example.of(probe);
                return this.rolesRepository.findOne(ex).orElse(null);
            });
            
            var permissionsTask = CompletableFuture.supplyAsync(() -> this.organizationPermissionsService.findAllByIds(permissionsIds));
            var role = roleTask.get();
            var permissions = permissionsTask.get();

            if(role == null) {
                throw new RuntimeException(String.format("role with id: '%s' was not found", roleId));
            }

            if(permissions == null || permissions.size() == 0) {
                throw new RuntimeException(String.format("no permissions were provided to be assigned to the role"));
            }

            role.getOrganizationPermissions().addAll(permissions);
            this.rolesRepository.save(role);

            return role;
        } catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException("Error occurred during task execution", ex);
        }
    }

    public OrganizationRole unAssignPermissionsFromRole(Integer roleId, Set<Integer> permissionsIds, Integer organizationId) {
        try {
            var roleTask = CompletableFuture.supplyAsync(() -> {
                var probe = new OrganizationRole();
                if(organizationId != null) {
                    probe.setOrganizationId(organizationId);
                }
                
                probe.setId(roleId);

                var ex = Example.of(probe);
                return this.rolesRepository.findOne(ex);
            });

            var permissionsTask = CompletableFuture.supplyAsync(() -> this.organizationPermissionsService.findAllByIds(permissionsIds));
            var roleOpt = roleTask.get();
            var role = roleOpt.get();
            var permissions = permissionsTask.get();

            if(role == null) {
                throw new RuntimeException(String.format("role with id: '%s' was not found", roleId));
            }

            if(permissions == null || permissions.size() == 0) {
                throw new RuntimeException(String.format("no permissions were provided to be assigned to the role"));
            }

            role.getOrganizationPermissions().addAll(permissions);
            this.rolesRepository.save(role);

            return role;
        } catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException("Error occurred during task execution", ex);
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
}
