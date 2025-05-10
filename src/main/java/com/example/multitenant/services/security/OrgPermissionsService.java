package com.example.multitenant.services.security;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import com.example.multitenant.models.*;
import com.example.multitenant.models.binders.*;
import com.example.multitenant.models.enums.*;
import com.example.multitenant.repository.*;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OrgPermissionsService {
    
    private final OrgPermissionsRepository orgPermissionsRepository;
    private final MembershipRepository orgsMembershipRepository;

    public Page<OrgPermission> findAllPermissions(Integer page, Integer size) {
        var pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
        var result = this.orgPermissionsRepository.findAll(pageable);

        return result;
    }

    public OrgPermission findByName(String name) {
        return this.orgPermissionsRepository.findByName(name).orElse(null);
    }

    public List<OrgPermission> findAllByIds(Set<Integer> ids) {
        var result = this.orgPermissionsRepository.findAllById(ids);
        return result;
    }

    public Set<OrgPermission> findAllDefaultPermissions(DefaultOrganizationRole role) {
        var probe = new OrgPermission();
        if(role == DefaultOrganizationRole.ORG_OWNER) {
            probe.setIsDefaultOrgOwner(true);
        } else if (role == DefaultOrganizationRole.ORG_ADMIN) {
            probe.setIsDefaultAdmin(true);
        } else if (role == DefaultOrganizationRole.ORG_USER) {
            probe.setIsDefaultUser(true);
        } else {
            // TODO: will be handled later
        }

        return this.orgPermissionsRepository.findAll(Example.of(probe)).stream().map((s) -> s).collect(Collectors.toSet());
    }

    public boolean hasPermission(long userId, Integer tenantId, String... permissions) {
        var key = new MembershipKey(tenantId, userId);

        var membership = this.orgsMembershipRepository.findById(key).orElse(null);
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
