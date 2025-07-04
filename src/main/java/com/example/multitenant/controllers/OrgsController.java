package com.example.multitenant.controllers;

import com.example.multitenant.common.annotations.contract.CheckRestricted;
import com.example.multitenant.common.resolvers.contract.HandleSize;
import com.example.multitenant.common.validators.contract.ValidateNumberId;
import com.example.multitenant.dtos.apiresponse.ApiResponses;
import com.example.multitenant.dtos.auth.UserPrincipal;
import com.example.multitenant.dtos.organizations.*;
import com.example.multitenant.dtos.users.UsersFilter;
import com.example.multitenant.models.enums.FilesPath;
import com.example.multitenant.models.enums.LogEventType;
import com.example.multitenant.services.files.FilesService;
import com.example.multitenant.services.logs.LogsService;
import com.example.multitenant.services.membership.MemberShipService;
import com.example.multitenant.services.organizations.OrgsService;
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
public class OrgsController {
    
    private final OrgsService organizationsService;
    private final MemberShipService memberShipService;
    private final LogsService logsService;

    @CheckRestricted
    @PostMapping("")
    @PreAuthorize("hasAuthority(@globalPermissions.ORG_CREATE)")
    public ResponseEntity<Object> createOrganization(@Valid @ModelAttribute OrgCreateDTO dto) {
        if (this.organizationsService.existsByName(dto.name())) {
            return ResponseEntity.badRequest().body(ApiResponses.GetErrResponse("organization name was taken already"));
        }
        var user = SecurityUtils.getPrincipal().getUser();
        var membership = this.memberShipService.initializeOrganizationWithMembership(dto.toModel(), user, dto.image());

        var respBody = ApiResponses.OneKey("membership", membership.toViewDTO());
        return ResponseEntity.status(HttpStatus.CREATED).body(respBody);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchOrganizations(
        @RequestParam(required = false) Long cursorId, @HandleSize @RequestParam(defaultValue = "10") Integer size,
        OrgsFilter filter) {
        
        var result = this.organizationsService.search(filter, cursorId, size);
        var body = result.toApiResponse("organizations", (orgs) -> orgs.stream().map((org) -> org.toSearchDTO()).toList());
        
        return ResponseEntity.ok(body);
    }

    @CheckRestricted
    @PatchMapping("/memberships/{userId}/kick")
    @PreAuthorize("@customSPEL.hasOrgAuthority(@orgPermissions.USER_KICK)")
    public ResponseEntity<Object> kickUser(@ValidateNumberId @PathVariable long userId) {
        var orgId = AppUtils.getTenantId();
        var user = SecurityUtils.getUserFromAuth();
        
        this.memberShipService.kickUserFromOrganization(orgId, userId);
        this.logsService.createKickLog(user, userId, orgId, LogEventType.KICK);

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PatchMapping("/leave/{organizationId}")
    public ResponseEntity<Object> leaveOrganization() {
        var orgId = AppUtils.getTenantId();
        var user = SecurityUtils.getUserFromAuth();

        this.memberShipService.kickUserFromOrganization(orgId, user.getId());
        this.logsService.createMembershipLog(user, orgId, LogEventType.LEAVE);

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
