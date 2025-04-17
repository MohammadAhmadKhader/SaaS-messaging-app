package com.example.multitenant.controllers.organizationDashboard;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.multitenant.common.validators.contract.ValidateNumberId;
import com.example.multitenant.dtos.apiResponse.ApiResponses;
import com.example.multitenant.dtos.organizations.OrganizationUpdateDTO;
import com.example.multitenant.models.Organization;
import com.example.multitenant.services.organizations.OrganizationsService;

import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/organizations/dashboard")
public class OrganizationDashboardController {
    private final OrganizationsService organizationsService;

    public OrganizationDashboardController(OrganizationsService organizationsService) {
        this.organizationsService = organizationsService;
    }

    @PutMapping("/{id}")
    @PreAuthorize("@customSPEL.hasOrgAuthority(authentication, @globalPermissions.DASH_ORGANIZATION_UPDATE)")
    public ResponseEntity<Object> updateOrganization(@ValidateNumberId @PathVariable Integer id, @Valid @RequestBody OrganizationUpdateDTO dto) {
        var updatedOrg = this.organizationsService.findThenUpdate(id, dto.toModel());
        var respBody = ApiResponses.OneKey("organization", updatedOrg.toViewDTO());
        
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(respBody);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@customSPEL.hasOrgRole(authentication, 'Org-Owner')")
    public ResponseEntity<Object> deleteOrganization(@ValidateNumberId @PathVariable Integer id) {
        var isDeleted = this.organizationsService.findThenDeleteById(id);
        if(!isDeleted) {
            return ResponseEntity.badRequest().body(ApiResponses.GetNotFoundErr("organization", id));
        }
        
        return ResponseEntity.noContent().build();
    }

}
