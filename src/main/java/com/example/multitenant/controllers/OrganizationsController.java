package com.example.multitenant.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.example.multitenant.dtos.apiResponse.ApiResponses;
import com.example.multitenant.dtos.auth.UserPrincipal;
import com.example.multitenant.dtos.organizations.OrganizationCreateDTO;
import com.example.multitenant.dtos.organizations.OrganizationUpdateDTO;
import com.example.multitenant.services.membership.MemberShipService;
import com.example.multitenant.services.organizations.OrganizationsService;
import com.example.multitenant.utils.SecurityUtils;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/organizations")
public class OrganizationsController {
    
    private final OrganizationsService organizationsService;
    private final MemberShipService memberShipService;
    
    public OrganizationsController(OrganizationsService organizationsService, MemberShipService memberShipService) {
        this.organizationsService = organizationsService;
        this.memberShipService = memberShipService;
    }

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
}
