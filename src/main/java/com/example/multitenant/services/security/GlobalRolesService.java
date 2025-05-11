package com.example.multitenant.services.security;

import java.util.Set;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import com.example.multitenant.exceptions.*;
import com.example.multitenant.models.GlobalRole;
import com.example.multitenant.repository.GlobalRolesRepository;
import com.example.multitenant.services.security.helperservices.GlobalRolesCrudService;
import com.example.multitenant.utils.VirtualThreadsUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class GlobalRolesService {
    private final GlobalRolesRepository globalRolesRepository;
    private final GlobalPermissionsService globalPermissionsService;
    private final GlobalRolesCrudService globalRolesCrudService;

    public Page<GlobalRole> findAllRoles(Integer page, Integer size) {
        var pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
        Page<GlobalRole> result = this.globalRolesRepository.findAll(pageable);

        return result;
    }

    public GlobalRole findByName(String name) {
        return this.globalRolesRepository.findByName(name).orElse(null);
    }

    public GlobalRole assignPermissionsToRole(Integer roleId, Set<Integer> permissionsIds) {
        var tasksResults = VirtualThreadsUtils.run(
            () -> this.globalRolesRepository.findById(roleId).orElse(null),
            () -> this.globalPermissionsService.findAllByIds(permissionsIds)
        );

        var role = tasksResults.getLeft();
        var permissions = tasksResults.getRight();

        if(role == null) {
            throw new ResourceNotFoundException("role", roleId);
        }

        if(permissions == null || permissions.size() == 0) {
            throw new InvalidOperationException("no permissions were provided to be assigned to the role");
        }

        role.getPermissions().addAll(permissions);
        this.globalRolesRepository.save(role);

        return role;
    }

    public GlobalRole unAssignPermissionsFromRole(Integer roleId, Set<Integer> permissionsIds) {
        var tasksResults = VirtualThreadsUtils.run(
            () -> this.globalRolesRepository.findById(roleId).orElse(null), 
            () -> this.globalPermissionsService.findAllByIds(permissionsIds)
        );
    
        var role = tasksResults.getLeft();
        var permissions = tasksResults.getRight();

        if(role == null) {
            throw new ResourceNotFoundException("role", roleId);
        }

        if(permissions == null || permissions.size() == 0) {
            throw new InvalidOperationException("no permissions were provided to be assigned to the role");
        }

        role.getPermissions().addAll(permissions);
        this.globalRolesRepository.save(role);

        return role;
    }

    public void deleteRole(Integer roleId) {
        var role = this.globalRolesRepository.findById(roleId).orElse(null);
        if (role == null) {
            throw new ResourceNotFoundException("role", roleId);
        }

        this.globalRolesRepository.delete(role);
    }

    public GlobalRole findById(Integer roleId) {
        return this.globalRolesCrudService.findById(roleId);
    }

    public void deleteById(Integer roleId) {
        this.globalRolesCrudService.deleteById(roleId);
    }

    public GlobalRole findThenUpdate(Integer roleId, GlobalRole globalRole) {
        return this.globalRolesCrudService.findThenUpdate(roleId, (existingRole) -> patcher(existingRole, globalRole));
    }

    public GlobalRole create(GlobalRole role) {
        return this.globalRolesCrudService.create(role);
    }

    public void patcher(GlobalRole target, GlobalRole source) {
        var newName = source.getName();
        var newDisplayName = source.getDisplayName();

        if(newName != null) {
            target.setName(newName);
        }

        if(newDisplayName != null) {
            target.setDisplayName(newDisplayName);
        }
    }
}
