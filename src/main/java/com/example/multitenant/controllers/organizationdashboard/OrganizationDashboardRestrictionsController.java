package com.example.multitenant.controllers.organizationdashboard;

import java.time.Instant;

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

import com.example.multitenant.common.annotations.contract.CheckRestricted;
import com.example.multitenant.common.resolvers.contract.HandlePage;
import com.example.multitenant.common.resolvers.contract.HandleSize;
import com.example.multitenant.common.validators.contract.ValidateNumberId;
import com.example.multitenant.dtos.apiresponse.ApiResponses;
import com.example.multitenant.dtos.organizationrestrictions.OrgRestrictionCreateDTO;
import com.example.multitenant.dtos.organizationrestrictions.OrgRestrictionUpdateDTO;
import com.example.multitenant.dtos.organizationrestrictions.OrgRestrictionsFilter;
import com.example.multitenant.dtos.restrictions.RestrictionCreateDTO;
import com.example.multitenant.dtos.restrictions.RestrictionUpdateDTO;
import com.example.multitenant.services.cache.OrgRestrictionsCacheSerivce;
import com.example.multitenant.services.organizationsrestrictions.OrganizationRestrictionsService;
import com.example.multitenant.utils.AppUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@CheckRestricted
@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/organizations/dashboard/restrictions")
public class OrganizationDashboardRestrictionsController {
    private final OrganizationRestrictionsService restrictionsService;
    private final OrgRestrictionsCacheSerivce orgRestrictionsCacheSerivce;

    @GetMapping("")
    @PreAuthorize("@customSPEL.hasOrgAuthority(@organizationPermissions.RESTRICTION_VIEW)")
    public ResponseEntity<Object> getRestrictions(@HandlePage Integer page, @HandleSize Integer size, OrgRestrictionsFilter filter) {
        var tenantId = AppUtils.getTenantId();
            
        var restrictions = this.restrictionsService.getRestrictions(page, size, tenantId, filter);
        var count = restrictions.getTotalElements();
        var restrictionsViews = restrictions.map((rest) -> {
            return rest.toViewDTO();
        }).toList();
        
        var res = ApiResponses.GetAllResponse("restrictions", restrictionsViews, count, page, size);
        
        return ResponseEntity.ok().body(res);
    }

    @PostMapping("")
    @PreAuthorize("@customSPEL.hasOrgAuthority(@organizationPermissions.RESTRICTION_CREATE)")
    public ResponseEntity<Object> handleCreateRestriction(@Valid @RequestBody OrgRestrictionCreateDTO dto) {
        var tenantId = AppUtils.getTenantId();
        var restrcitedUserId = dto.getUserId();

        var restriction = this.restrictionsService.restrictUser(restrcitedUserId, tenantId, dto.toModel());
        this.orgRestrictionsCacheSerivce.invalidateKey(tenantId, restrcitedUserId);

        var respBody = ApiResponses.OneKey("restriction", restriction.toViewDTO());

        return ResponseEntity.status(HttpStatus.CREATED).body(respBody);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("@customSPEL.hasOrgAuthority(@organizationPermissions.RESTRICTION_UPDATE)")
    public ResponseEntity<Object> handleUpdateRestriction(@ValidateNumberId @PathVariable Integer id,
     @Valid @RequestBody OrgRestrictionUpdateDTO dto) {
        var tenantId = AppUtils.getTenantId();

        var rest = this.restrictionsService.updateRestriction(id, tenantId, dto.toModel());

        var until = rest.getUntil();
        var now = Instant.now();
        var isRestRemoved = until.isBefore(now) || until.equals(now);
        if(isRestRemoved) {
            this.orgRestrictionsCacheSerivce.invalidateKey(tenantId, rest.getUserId());
        }

        var respBody = ApiResponses.OneKey("restriction", rest.toViewDTO());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(respBody);
    }
}