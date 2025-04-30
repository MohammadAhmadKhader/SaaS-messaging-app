package com.example.multitenant.services.security;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import com.example.multitenant.models.GlobalPermission;
import com.example.multitenant.repository.GlobalPermissionsRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class GlobalPermissionsService {
    private final GlobalPermissionsRepository globalPermissionsRepository;

    public Page<GlobalPermission> findAllPermissions(Integer page, Integer size) {
        var pageable = PageRequest.of(page - 1, size, Sort.by( "id").descending());
        var result = this.globalPermissionsRepository.findAll(pageable);

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
