package com.example.demo.services.security;

import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.demo.dtos.roles.RoleViewDTO;
import com.example.demo.dtos.shared.FindAllResult;
import com.example.demo.models.Role;
import com.example.demo.repository.roles.RolesRepository;
import com.example.demo.services.generic.GenericService;

import jakarta.transaction.Transactional;

@Service
public class RolesService extends GenericService<Role, Integer, RoleViewDTO> {
    private final RolesRepository rolesRepository;
    private final PermissionsService permissionsService;

    public RolesService(RolesRepository rolesRepository, PermissionsService permissionsService) {
        super(rolesRepository);
        this.rolesRepository = rolesRepository;
        this.permissionsService = permissionsService;
    }

    public FindAllResult<RoleViewDTO> findAllRoles(Integer page, Integer size) {
        var pageable = PageRequest.of(page - 1, size);
        var result = this.rolesRepository.findAll(pageable);
        var count = result.getTotalElements();

        var orgsView = result.getContent().stream().map((org) -> {
            return org.toViewDTO();
        }).toList();

        return new FindAllResult<>(orgsView, count, page, size);
    }

    public Role findByName(String name) {
        var optional = this.rolesRepository.findByName(name);
        if(!optional.isPresent()) {
            return null;
        }

        return optional.get();
    }

    public RoleViewDTO findByNameAsView(String name) {
        return this.findByName(name).toViewDTO();
    }

    @Transactional
    public RoleViewDTO assignPermissionsToRole(Integer roleId, Set<Integer> permissionsIds) {
        try(var scope = new StructuredTaskScope.ShutdownOnFailure())  {
            var roleTask = scope.fork(()-> findById(roleId));
            var permissionTask = scope.fork(() -> this.permissionsService.findAllByIds(permissionsIds));

            scope.join();
            scope.throwIfFailed();

            var role = roleTask.get();
            var permissions = permissionTask.get();

            if(role == null) {
                throw new RuntimeException(String.format("role with id: '%s' was not found", roleId));
            }

            if(permissions == null || permissions.size() == 0) {
                throw new RuntimeException(String.format("no permissions were provided to be assigned to the role"));
            }

            role.getPermissions().addAll(permissions);
            this.rolesRepository.save(role);

            return role.toViewDTO();
        } catch(InterruptedException | ExecutionException ex) {
            throw new RuntimeException("Error occurred during task execution", ex);
        }
    }
    
}
