package com.example.multitenant.services.security;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.multitenant.exceptions.ResourceNotFoundException;
import com.example.multitenant.models.GlobalRole;
import com.example.multitenant.repository.GlobalRolesRepository;
import com.example.multitenant.services.generic.GenericService;

@Service
public class GlobalRolesService extends GenericService<GlobalRole, Integer> {
    private final GlobalRolesRepository globalRolesRepository;
    private final GlobalPermissionsService globalPermissionsService;

    public GlobalRolesService(GlobalRolesRepository globalRolesRepository, GlobalPermissionsService globalPermissionsService) {
        super(globalRolesRepository);
        this.globalRolesRepository = globalRolesRepository;
        this.globalPermissionsService = globalPermissionsService;
    }

    public Page<GlobalRole> findAllRoles(Integer page, Integer size) {
        var pageable = PageRequest.of(page - 1, size);
        Page<GlobalRole> result = this.globalRolesRepository.findAll(pageable);

        return result;
    }

    public GlobalRole findByName(String name) {
        return this.globalRolesRepository.findByName(name).orElse(null);
    }

    public GlobalRole assignPermissionsToRole(Integer roleId, Set<Integer> permissionsIds) {
        try {
            var roleTask = CompletableFuture.supplyAsync(() -> {
                return this.globalRolesRepository.findById(roleId);
            });
            var permissionsTask = CompletableFuture.supplyAsync(() -> this.globalPermissionsService.findAllByIds(permissionsIds));

            var roleOpt = roleTask.get();
            var role = roleOpt.get();
            var permissions = permissionsTask.get();

            if(role == null) {
                throw new RuntimeException(String.format("role with id: '%s' was not found", roleId));
            }

            if(permissions == null || permissions.size() == 0) {
                throw new RuntimeException(String.format("no permissions were provided to be assigned to the role"));
            }

            role.getPermissions().addAll(permissions);
            this.globalRolesRepository.save(role);

            return role;
        } catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException("Error occurred during task execution", ex);
        }
    }

    public GlobalRole unAssignPermissionsFromRole(Integer roleId, Set<Integer> permissionsIds) {
        try {
            var roleTask = CompletableFuture.supplyAsync(() -> {
                return this.globalRolesRepository.findById(roleId);
            });
            var permissionsTask = CompletableFuture.supplyAsync(() -> this.globalPermissionsService.findAllByIds(permissionsIds));

            var roleOpt = roleTask.get();
            var role = roleOpt.get();
            var permissions = permissionsTask.get();

            if(role == null) {
                throw new RuntimeException(String.format("role with id: '%s' was not found", roleId));
            }

            if(permissions == null || permissions.size() == 0) {
                throw new RuntimeException(String.format("no permissions were provided to be assigned to the role"));
            }

            role.getPermissions().addAll(permissions);
            this.globalRolesRepository.save(role);

            return role;
        } catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException("Error occurred during task execution", ex);
        }
    }

    public void deleteRole(Integer roleId) {
        var role = this.globalRolesRepository.findById(roleId).orElse(null);
        if (role == null) {
            throw new ResourceNotFoundException("role", roleId);
        }

        this.globalRolesRepository.delete(role);
    }

    public GlobalRole findOne(Integer roleId) {
        return this.globalRolesRepository.findById(roleId).orElse(null);
    }
}
