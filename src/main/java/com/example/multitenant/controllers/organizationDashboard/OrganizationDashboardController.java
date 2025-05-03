package com.example.multitenant.controllers.organizationdashboard;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.multitenant.common.resolvers.contract.HandlePage;
import com.example.multitenant.common.resolvers.contract.HandleSize;
import com.example.multitenant.common.validators.contract.ValidateNumberId;
import com.example.multitenant.dtos.apiresponse.ApiResponses;
import com.example.multitenant.dtos.membership.MembershipFilter;
import com.example.multitenant.dtos.organizations.OrganizationTransferOwnershipDTO;
import com.example.multitenant.dtos.organizations.OrganizationUpdateDTO;
import com.example.multitenant.exceptions.InvalidOperationException;
import com.example.multitenant.exceptions.ResourceNotFoundException;
import com.example.multitenant.models.Organization;
import com.example.multitenant.services.membership.MemberShipService;
import com.example.multitenant.services.organizations.OrganizationsService;
import com.example.multitenant.services.users.UsersService;
import com.example.multitenant.utils.AppUtils;
import com.example.multitenant.utils.SecurityUtils;

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
        var updatedOrg = this.organizationsService.findThenUpdate(id, dto.toModel(), dto.image());
        var respBody = ApiResponses.OneKey("organization", updatedOrg.toViewDTO());
        
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(respBody);
    }

    @PatchMapping("/transfer-ownership")
    @PreAuthorize("@customSPEL.hasOrgRole('Org-Owner')")
    public ResponseEntity<Object> transferOwnership(@Valid @RequestBody OrganizationTransferOwnershipDTO dto) {
        var tenantId = AppUtils.getTenantId();
        var user = SecurityUtils.getUserFromAuth();
        this.memberShipService.swapOwnerShip(tenantId, user, dto.getNewOwnerId());
        
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/members")
    @PreAuthorize("hasAuthority(@globalPermissions.DASH_USER_VIEW)")
    public ResponseEntity<Object> getOrganizationActiveMemberships(@HandlePage Integer page, @HandleSize Integer size,
        @RequestParam(defaultValue = "joinedAt") String sortBy, @RequestParam(defaultValue = "DESC") String sortDir,
        @Valid MembershipFilter filters
    ) {
        var tenantId = AppUtils.getTenantId();

        var memberships = this.memberShipService.findActiveOrgMemberShips(tenantId, page, size, sortBy, sortDir, filters);
        var count = memberships.getTotalElements();
        var membershipsViews = memberships.map((membership) -> {
            return membership.toViewDTO();
        }).toList();

        var res = ApiResponses.GetAllResponse("members", membershipsViews, count, page, size);

        return ResponseEntity.ok().body(res);
    }

    @DeleteMapping("")
    @PreAuthorize("@customSPEL.hasOrgRole('Org-Owner')")
    public ResponseEntity<Object> deleteOrganization() {
        var tenantId = AppUtils.getTenantId();
        var org = this.organizationsService.findByIdThenDelete(tenantId);
        if(org == null) {
            return ResponseEntity.badRequest().body(ApiResponses.GetNotFoundErr("organization", tenantId));
        }
        
        return ResponseEntity.noContent().build();
    }

}
