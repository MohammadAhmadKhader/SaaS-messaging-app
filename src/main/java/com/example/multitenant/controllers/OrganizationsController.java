package com.example.multitenant.controllers;

import com.example.multitenant.common.validators.contract.ValidateNumberId;
import com.example.multitenant.dtos.apiresponse.ApiResponses;
import com.example.multitenant.dtos.auth.UserPrincipal;
import com.example.multitenant.dtos.organizations.*;
import com.example.multitenant.models.enums.FilesPath;
import com.example.multitenant.models.enums.LogEventType;
import com.example.multitenant.services.files.FilesService;
import com.example.multitenant.services.logs.LogsService;
import com.example.multitenant.services.membership.MemberShipService;
import com.example.multitenant.services.organizations.OrganizationsService;
import com.example.multitenant.utils.AppUtils;
import com.example.multitenant.utils.SecurityUtils;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/organizations")
public class OrganizationsController {
    
    private final OrganizationsService organizationsService;
    private final MemberShipService memberShipService;
    private final LogsService logsService;

    @PostMapping("")
    @PreAuthorize("hasAuthority(@globalPermissions.ORG_CREATE)")
    public ResponseEntity<Object> createOrganization(@Valid @ModelAttribute OrganizationCreateDTO dto) {

        log.info("received this image {}", dto.image().getOriginalFilename());
        if (this.organizationsService.existsByName(dto.name())) {
            return ResponseEntity.badRequest().body(ApiResponses.GetErrResponse("organization name was taken already"));
        }
        var user = SecurityUtils.getPrincipal().getUser();
        var membership = this.memberShipService.initializeOrganizationWithMembership(dto.toModel(), user, dto.image());

        var respBody = ApiResponses.OneKey("membership", membership.toViewDTO());
        return ResponseEntity.status(HttpStatus.CREATED).body(respBody);
    }

    @PatchMapping("/memberships/{userId}/kick")
    @PreAuthorize("@customSPEL.hasOrgAuthority(@organizationPermissions.USER_KICK)")
    public ResponseEntity<Object> kickUser(@ValidateNumberId @PathVariable long userId) {
        var orgId = AppUtils.getTenantId();
        var user = SecurityUtils.getUserFromAuth();

        var membership = this.memberShipService.findOne(orgId, userId);
        if(!membership.isMember()) {
            return ResponseEntity.badRequest().body(ApiResponses.GetErrResponse("user is not part of the organization"));
        }
        
        this.memberShipService.kickUserFromOrganization(membership.getId().getOrganizationId(), membership.getId().getUserId());
        this.logsService.createKickLog(user, userId, orgId, LogEventType.KICK);

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PatchMapping("/leave/{organizationId}")
    public ResponseEntity<Object> leaveOrganization() {
        var orgId = AppUtils.getTenantId();
        var user = SecurityUtils.getUserFromAuth();

        var membership = this.memberShipService.findOne(orgId, user.getId());
        if(!membership.isMember()) {
            return ResponseEntity.badRequest().body(ApiResponses.GetErrResponse("user is not part of the organization"));
        }

        var org = this.organizationsService.findOneWithOwner(orgId);
        if(org.getOwner().getId() == user.getId()) {
            return ResponseEntity.badRequest().body(ApiResponses.GetErrResponse("can not leave an owned organization you have to delete it, or transfer ownership first"));
        }

        this.memberShipService.kickUserFromOrganization(membership.getId().getOrganizationId(), membership.getId().getUserId());
        this.logsService.createKickLog(user, user.getId(), orgId, LogEventType.LEAVE);

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
