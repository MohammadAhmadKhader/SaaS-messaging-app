package com.example.multitenant.services.organizationsrestrictions;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.multitenant.dtos.organizationrestrictions.OrgRestrictionsFilter;
import com.example.multitenant.exceptions.InvalidOperationException;
import com.example.multitenant.exceptions.ResourceNotFoundException;
import com.example.multitenant.models.OrganizationRestriction;
import com.example.multitenant.models.User;
import com.example.multitenant.models.enums.DefaultOrganizationRole;
import com.example.multitenant.repository.OrganizationRestrictionsRepository;
import com.example.multitenant.services.membership.MemberShipService;
import com.example.multitenant.services.organizations.OrganizationsService;
import com.example.multitenant.services.users.UsersService;
import com.example.multitenant.specificationsbuilders.OrganizationsRestrictionsSpecificationsBuilder;
import com.example.multitenant.specificationsbuilders.OrganizationsSpecificationsBuilder;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OrganizationRestrictionsService {
    private final OrganizationRestrictionsRepository organizationRestrictionsRepository;
    private final OrganizationRestrictionsSpecificationsService specificationsService;
    private final MemberShipService memberShipService;
    private final UsersService usersService;

    public Page<OrganizationRestriction> getRestrictions(Integer page, Integer size, Integer tenantId, OrgRestrictionsFilter filter) {
        var pageRequest = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        filter.setOrganizationId(tenantId);
        
        var spec = OrganizationsRestrictionsSpecificationsBuilder.build(filter);
        var result  = this.specificationsService.findAllWithSpecifications(pageRequest, spec, null);

        return result;
    }

    public boolean isUserRestricted(Long userId, Integer orgId) {
        return this.organizationRestrictionsRepository.isUserRestricted(userId, orgId);
    }

    @Transactional
    public OrganizationRestriction restrictUser(Long userId, Integer orgId, OrganizationRestriction dto) {
        var membership = this.memberShipService.findUserMembershipWithRoles(orgId, userId);
        User userToRestrict = null;
        if(membership == null) {
            // when user not signed inorganization
            userToRestrict = this.usersService.findById(userId);
            if(userToRestrict == null) {
                throw new ResourceNotFoundException("user", userId);
            }

        } else {
            // handles case where user is inside organization
            userToRestrict = membership.getUser();
            var orgOwnerRole = DefaultOrganizationRole.ORG_OWNER.getRoleName();
            var isOrgOwner = membership.getOrganizationRoles().stream().anyMatch((r) -> r.getName().equals(orgOwnerRole));
            if(isOrgOwner) {
                throw new InvalidOperationException("can not restrict organization owner");
            }

            this.memberShipService.kickUserFromOrganization(orgId, userToRestrict.getId());
        }    

        var restriction = new OrganizationRestriction();
        restriction.setUser(userToRestrict);
        restriction.setOrganizationId(orgId);
        restriction.setUntil(dto.getUntil());
        restriction.setReason(dto.getReason());

        return this.organizationRestrictionsRepository.save(restriction);
    }

    public OrganizationRestriction updateRestriction(Integer id, Integer orgId, OrganizationRestriction dto) {
        var restriction = this.organizationRestrictionsRepository.findByIdAndOrgId(id, orgId);
        if(restriction == null) {
            throw new ResourceNotFoundException("restriction", id);
        }

        this.patcher(restriction, dto);

        return this.organizationRestrictionsRepository.save(restriction);
    }

    private void patcher(OrganizationRestriction target, OrganizationRestriction source) {
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
