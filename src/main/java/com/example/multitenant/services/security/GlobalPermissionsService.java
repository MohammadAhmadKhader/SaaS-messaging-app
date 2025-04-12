package com.example.multitenant.services.security;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.multitenant.models.GlobalPermission;
import com.example.multitenant.repository.GlobalPermissionsRepository;
import com.example.multitenant.services.generic.GenericService;

@Service
public class GlobalPermissionsService extends GenericService<GlobalPermission, Integer> {
    
    private final GlobalPermissionsRepository globalPermissionsRepository;

    public GlobalPermissionsService(GlobalPermissionsRepository globalPermissionsRepository) {
        super(globalPermissionsRepository);
        this.globalPermissionsRepository = globalPermissionsRepository;
    }

    public Page<GlobalPermission> findAllPermissions(Integer page, Integer size) {
        var pageable = PageRequest.of(page - 1, size);
        var result = this.globalPermissionsRepository.findAll(pageable);
        var count = result.getTotalElements();

        return result;
    }

    public GlobalPermission findByName(String name) {
        return this.globalPermissionsRepository.findByName(name).orElse(null);
    }

    public List<GlobalPermission> findAllByIds(Set<Integer> ids) {
        var result = this.globalPermissionsRepository.findAllById(ids);
        return result;
    }
}
