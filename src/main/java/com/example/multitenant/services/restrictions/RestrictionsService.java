package com.example.multitenant.services.restrictions;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import com.example.multitenant.dtos.restrictions.RestrictionsFilter;
import com.example.multitenant.exceptions.InvalidOperationException;
import com.example.multitenant.exceptions.ResourceNotFoundException;
import com.example.multitenant.models.Restriction;
import com.example.multitenant.models.User;
import com.example.multitenant.models.enums.DefaultGlobalRole;
import com.example.multitenant.repository.RestrictionsRepository;
import com.example.multitenant.services.users.UsersService;
import com.example.multitenant.specificationsbuilders.RestrictionsSpecBuilder;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RestrictionsService {
    private final RestrictionsCrudService restrictionsCrudService;
    private final RestrictionsRepository restrictionsRepository;
    private final RestrictionsSpecService specificationsService;
    private final UsersService usersService;

    public Page<Restriction> getRestrictions(Integer page, Integer size, RestrictionsFilter filter) {
        var pageRequest = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        var spec = RestrictionsSpecBuilder.build(filter);
        var result  = this.specificationsService.findAllWithSpecifications(pageRequest, spec, null);

        return result;
    }

    public boolean isUserRestricted(Long userId) {
        return this.restrictionsRepository.isUserRestricted(userId);
    }

    public Restriction restrictUser(Long userId, Restriction dto) {
        var user = this.usersService.findUserWithRoles(userId);
        if(user == null) {
            throw new ResourceNotFoundException("user", userId);
        }

        var superAdmin = DefaultGlobalRole.SUPERADMIN.getRoleName();
        var hasSuperAdminRole = user.getRoles().stream().anyMatch((r) -> r.getName().equals(superAdmin));
        if(hasSuperAdminRole) {
            throw new InvalidOperationException("can not restrict a user with super admin role");
        }

        var isAlreadyRestricted = this.restrictionsRepository.isUserRestricted(userId);
        if(isAlreadyRestricted) {
            throw new InvalidOperationException("user already has active restriction");
        }

        var restriction = new Restriction();
        restriction.setUser(user);
        restriction.setUntil(dto.getUntil());
        restriction.setReason(dto.getReason());

        return this.restrictionsRepository.save(restriction);
    }

    public Restriction updateRestriction(Integer id, Restriction dto) {
        var restriction = this.restrictionsCrudService.findThenUpdate(id, (existingRest) -> this.patcher(existingRest, dto));
        if(restriction == null) {
            throw new ResourceNotFoundException("restriction", id);
        }

        return restriction;
    }

    private void patcher(Restriction target, Restriction source) {
        var newReason = source.getReason();
        var newUtil = source.getUntil();

        if(newReason != null) {
            target.setReason(newReason);
        }

        if(newUtil != null) {
            target.setUntil(newUtil);
        }
    }
}