package com.example.multitenant.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.example.multitenant.common.validators.contract.ValidateNumberId;
import com.example.multitenant.dtos.apiResponse.ApiResponses;
import com.example.multitenant.dtos.auth.UserPrincipal;
import com.example.multitenant.dtos.organizations.*;
import com.example.multitenant.services.membership.MemberShipService;
import com.example.multitenant.services.organizations.OrganizationsService;
import com.example.multitenant.utils.AppUtils;
import com.example.multitenant.utils.SecurityUtils;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/organizations")
public class OrganizationsController {
    
    private final OrganizationsService organizationsService;
    private final MemberShipService memberShipService;

    @PostMapping("")
    @PreAuthorize("hasAuthority(@globalPermissions.ORG_CREATE)")
    public ResponseEntity<Object> createOrganization(@Valid @RequestBody OrganizationCreateDTO dto) {
        if (this.organizationsService.existsByName(dto.name())) {
            return ResponseEntity.badRequest().body(ApiResponses.GetErrResponse("organization name was taken already"));
        }
        var org = this.organizationsService.create(dto.toModel());

        var user = SecurityUtils.getPrincipal().getUser();
        var membership = this.memberShipService.createOwnerMembership(org, user);

        var respBody = ApiResponses.OneKey("membership", membership.toViewDTO());
        return ResponseEntity.status(HttpStatus.CREATED).body(respBody);
    }

    @PatchMapping("/memberships/{userId}/kick")
    @PreAuthorize("@customSPEL.hasOrgAuthority(@organizationPermissions.USER_KICK)")
    public ResponseEntity<Object> kickUser(@ValidateNumberId @PathVariable long userId) {
        var orgId = AppUtils.getTenantId();
        var membership = this.memberShipService.findOne(orgId, userId);
        if(!membership.isMember()) {
            return ResponseEntity.badRequest().body(ApiResponses.GetErrResponse("user is not part of the organization"));
        }

        this.memberShipService.kickUserFromOrganization(membership.getId().getOrganizationId(), membership.getId().getUserId());

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
