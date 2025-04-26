package com.example.multitenant.controllers.organizationDashboard;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.multitenant.common.validators.contract.ValidateNumberId;
import com.example.multitenant.dtos.apiResponse.ApiResponses;
import com.example.multitenant.dtos.organizations.OrganizationTransferOwnershipDTO;
import com.example.multitenant.dtos.organizations.OrganizationUpdateDTO;
import com.example.multitenant.exceptions.InvalidOperationException;
import com.example.multitenant.exceptions.ResourceNotFoundException;
import com.example.multitenant.models.Organization;
import com.example.multitenant.services.membership.MemberShipService;
import com.example.multitenant.services.organizations.OrganizationsService;
import com.example.multitenant.utils.AppUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/organizations/dashboard")
public class OrganizationDashboardController {
    private final OrganizationsService organizationsService;
    private final MemberShipService memberShipService;

    @PutMapping("/{id}")
    @PreAuthorize("@customSPEL.hasOrgAuthority(@globalPermissions.DASH_ORGANIZATION_UPDATE)")
    public ResponseEntity<Object> updateOrganization(@ValidateNumberId @PathVariable Integer id, @Valid @RequestBody OrganizationUpdateDTO dto) {
        var updatedOrg = this.organizationsService.findThenUpdate(id, dto.toModel());
        var respBody = ApiResponses.OneKey("organization", updatedOrg.toViewDTO());
        
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(respBody);
    }

    @PatchMapping("/transfer-ownership")
    @PreAuthorize("@customSPEL.hasOrgRole('Org-Owner')")
    public ResponseEntity<Object> transferOwnership(@Valid @RequestBody OrganizationTransferOwnershipDTO dto) {
        var tenantId = AppUtils.getTenantId();
        var user = AppUtils.getUserFromAuth();
        this.memberShipService.swapOwnerShip(tenantId, user, dto.getNewOwnerId());
        
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@customSPEL.hasOrgRole('Org-Owner')")
    public ResponseEntity<Object> deleteOrganization(@ValidateNumberId @PathVariable Integer id) {
        var isDeleted = this.organizationsService.findThenDeleteById(id);
        if(!isDeleted) {
            return ResponseEntity.badRequest().body(ApiResponses.GetNotFoundErr("organization", id));
        }
        
        return ResponseEntity.noContent().build();
    }

}
