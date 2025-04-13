package com.example.multitenant.services.security;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.multitenant.models.OrganizationPermission;
import com.example.multitenant.models.binders.MembershipKey;
import com.example.multitenant.models.binders.MembershipKey;
import com.example.multitenant.repository.MembershipRepository;
import com.example.multitenant.repository.OrganizationPermissionsRepository;
import com.example.multitenant.services.generic.GenericService;

@Service
public class OrganizationPermissionsService extends GenericService<OrganizationPermission, Integer> {
    
    private final OrganizationPermissionsRepository organizationPermissionsRepository;
    private final MembershipRepository organizationsMembershipRepository;

    public OrganizationPermissionsService(OrganizationPermissionsRepository organizationPermissionsRepository, MembershipRepository organizationsMembershipRepository ) {
        super(organizationPermissionsRepository);
        this.organizationPermissionsRepository = organizationPermissionsRepository;
        this.organizationsMembershipRepository = organizationsMembershipRepository;
    }

    public Page<OrganizationPermission> findAllPermissions(Integer page, Integer size) {
        var pageable = PageRequest.of(page - 1, size);
        var result = this.organizationPermissionsRepository.findAll(pageable);

        return result;
    }

    public OrganizationPermission findByName(String name) {
        var optional = this.organizationPermissionsRepository.findByName(name);
        if(!optional.isPresent()) {
            return null;
        }

        return optional.get();
    }

    public List<OrganizationPermission> findAllByIds(Set<Integer> ids) {
        var result = this.organizationPermissionsRepository.findAllById(ids);
        return result;
    }

    public boolean hasPermission(long userId, Integer tenantId, String... permissions) {
        var key = new MembershipKey(tenantId, userId);

        var membership = this.organizationsMembershipRepository.findById(key).orElse(null);
        if(membership == null) {
            return false;
        }

        var permsList = Arrays.asList(permissions);
        return membership.getOrganizationRoles().stream().flatMap((role) -> {
            var perms = role.getOrganizationPermissions();
          
            return perms.stream();
        }).anyMatch((perm) -> permsList.contains(perm.getName()));
    }
}
