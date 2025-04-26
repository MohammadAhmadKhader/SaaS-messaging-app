package com.example.multitenant.controllers.organizationDashboard;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.multitenant.common.resolvers.contract.*;
import com.example.multitenant.common.validators.contract.ValidateNumberId;
import com.example.multitenant.dtos.apiResponse.ApiResponses;
import com.example.multitenant.services.contents.ContentsService;
import com.example.multitenant.services.organizations.OrganizationsService;
import com.example.multitenant.services.security.OrganizationRolesService;
import com.example.multitenant.utils.AppUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/organizations/dashboard/contents")
public class OrganizationDashboardContentsController {
    private final ContentsService contentsService;

    @GetMapping("/users/{userId}")
    @PreAuthorize("@customSPEL.hasOrgAuthority(@organizationPermissions.CONTENT_VIEW)")
    public ResponseEntity<Object> getContentsByUserId( @PathVariable @ValidateNumberId Long userId,
        @HandlePage Integer page, @HandleSize Integer size) {
        var tenantId = AppUtils.getTenantId();

        var contents = this.contentsService.findContentsByUserId(page, size,userId, tenantId);
        var count = contents.getTotalElements();
        var contentsViews = contents.stream().map((con) -> {
            return con.toViewDTO();
        }).toList();

        var bodyResponse = ApiResponses.GetAllResponse("contents", contentsViews, count, page, size);
        
        return ResponseEntity.ok().body(bodyResponse);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@customSPEL.hasOrgAuthority(@globalPermissions.CONTENT_DELETE)")
    public ResponseEntity<Object> deleteContent(@ValidateNumberId @PathVariable Integer id) {
        var tenantId = AppUtils.getTenantId();

        var content = this.contentsService.findByIdAndOrganizationId(id, tenantId);
        if(content == null) {
            return ResponseEntity.badRequest().body(ApiResponses.GetNotFoundErr("content", id));
        }

        this.contentsService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
