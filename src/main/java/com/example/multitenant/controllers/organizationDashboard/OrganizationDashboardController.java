package com.example.multitenant.controllers.organizationDashboard;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.multitenant.common.annotations.contract.AuthorizeOrg;
import com.example.multitenant.common.resolvers.contract.HandlePage;
import com.example.multitenant.common.resolvers.contract.HandleSize;
import com.example.multitenant.common.validators.contract.ValidateNumberId;
import com.example.multitenant.dtos.apiResponse.ApiResponses;
import com.example.multitenant.dtos.organizationroles.AssignOrganizationPermissionsDTO;
import com.example.multitenant.services.contents.ContentsService;
import com.example.multitenant.services.organizations.OrganizationsService;
import com.example.multitenant.services.security.OrganizationPermissions;
import com.example.multitenant.services.security.OrganizationRolesService;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/organizations/dashboard")
public class OrganizationDashboardController {
    private final ContentsService contentsService;
    private final OrganizationsService organizationsService;
    private final OrganizationRolesService organizationRolesService;

    public OrganizationDashboardController(ContentsService contentsService, OrganizationsService organizationsService, OrganizationRolesService organizationRolesService) {
        this.contentsService = contentsService;
        this.organizationsService = organizationsService;
        this.organizationRolesService = organizationRolesService;
    }

    @GetMapping("/users/{userId}")
    @AuthorizeOrg({OrganizationPermissions.DASH_CONTENT_VIEW})
    public ResponseEntity<Object> getContentsByUserId(
        @PathVariable @ValidateNumberId Long userId,
        @HandlePage Integer page, @HandleSize Integer size, 
        @RequestHeader("X-Tenant-ID") String tenantId) {
        
        var contents = this.contentsService.findContentsByUserId(page, size,userId, Integer.parseInt(tenantId));
        var count = contents.getTotalElements();
        var contentsViews = contents.stream().map((con) -> {
            return con.toViewDTO();
        }).toList();

        var bodyResponse = ApiResponses.GetAllResponse("contents", contentsViews, count, page, size);
        
        return ResponseEntity.ok().body(bodyResponse);
    }

    @PostMapping("/permissions/{id}")
    @AuthorizeOrg({OrganizationPermissions.ROLE_ASSIGN})
    public ResponseEntity<Object> assignPermissions(@ValidateNumberId @PathVariable Integer id, @Valid @RequestBody AssignOrganizationPermissionsDTO dto,
        @RequestHeader("X-Tenant-ID") String tenantId
    ) {
        var tenantIdAsInt = Integer.parseInt(tenantId);
        var organization = this.organizationsService.findById(tenantIdAsInt);
        if(organization == null) {
            var errRes = ApiResponses.GetErrResponse(String.format("tenant with id: '%s' was not found" ,tenantId));
            return ResponseEntity.badRequest().body(errRes);
        }

        var updatedRole = this.organizationRolesService.unAssignPermissionsFromRole(id, dto.getPermissionsIds(), tenantIdAsInt);
        var respBody = ApiResponses.OneKey("role", updatedRole.toViewDTO());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(respBody);
    }


    @DeleteMapping("/contents/{id}")
    @AuthorizeOrg({OrganizationPermissions.DASH_CONTENT_DELETE})
    public ResponseEntity<Object> deleteContent(@ValidateNumberId @PathVariable Integer id, @RequestHeader("X-Tenant-ID") String tenantId) {
        var content = this.contentsService.findByIdAndOrganizationId(id, Integer.parseInt(tenantId));
        if(content == null) {
            return ResponseEntity.badRequest().body(ApiResponses.GetNotFoundErr("content", id));
        }

        this.contentsService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
