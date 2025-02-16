package com.example.demo.services.security;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.demo.dtos.permissions.PermissionViewDTO;
import com.example.demo.dtos.shared.FindAllResult;
import com.example.demo.models.Permission;
import com.example.demo.repository.permissions.PermissionsRepository;
import com.example.demo.services.generic.GenericService;

@Service
public class PermissionsService extends GenericService<Permission, Integer, PermissionViewDTO> {
    
    private final PermissionsRepository permissionsRepository;

    public PermissionsService(PermissionsRepository permissionsRepository) {
        super(permissionsRepository);
        this.permissionsRepository = permissionsRepository;
    }

    public FindAllResult<PermissionViewDTO> findAllPermissions(Integer page, Integer size) {
        var pageable = PageRequest.of(page - 1, size);
        var result = this.permissionsRepository.findAll(pageable);
        var count = result.getTotalElements();

        var premViews = result.getContent().stream().map((org) -> {
            return org.toViewDTO();
        }).toList();

        return new FindAllResult<>(premViews, count, page, size);
    }

    public Permission findByName(String name) {
        var optional = this.permissionsRepository.findByName(name);
        if(!optional.isPresent()) {
            return null;
        }

        return optional.get();
    }

    public List<Permission> findAllByIds(Set<Integer> ids) {
        var result = this.permissionsRepository.findAllById(ids);
        return result;
    }
}
