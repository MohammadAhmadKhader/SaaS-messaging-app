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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.multitenant.common.resolvers.contract.HandlePage;
import com.example.multitenant.common.resolvers.contract.HandleSize;
import com.example.multitenant.common.validators.contract.ValidateNumberId;
import com.example.multitenant.dtos.apiResponse.ApiResponses;
import com.example.multitenant.dtos.organizationroles.AssignOrganizationPermissionsDTO;
import com.example.multitenant.dtos.organizationroles.AssignOrganizationRoleDTO;
import com.example.multitenant.dtos.organizationroles.OrganizationRoleCreateDTO;
import com.example.multitenant.dtos.organizationroles.OrganizationRoleUpdateDTO;
import com.example.multitenant.dtos.organizationroles.UnAssignOrganizationPermissionsDTO;
import com.example.multitenant.dtos.organizationroles.UnAssignOrganizationRoleDTO;
import com.example.multitenant.services.contents.ContentsService;
import com.example.multitenant.services.membership.MemberShipService;
import com.example.multitenant.services.organizations.OrganizationsService;
import com.example.multitenant.services.security.OrganizationRolesService;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/organizations/dashboard/roles")
public class OrganizationDashboardRolesController {
    private final OrganizationsService organizationsService;
    private final OrganizationRolesService organizationRolesService;
    private final MemberShipService memberShipService;

    public OrganizationDashboardRolesController(OrganizationsService organizationsService, OrganizationRolesService organizationRolesService, MemberShipService memberShipService) {
        this.organizationsService = organizationsService;
        this.organizationRolesService = organizationRolesService;
        this.memberShipService = memberShipService;
    }

    @GetMapping("")
    @PreAuthorize("@customSPEL.hasOrgAuthority(authentication, #tenantId, @organizationPermissions.ROLE_VIEW)")
    public ResponseEntity<Object> getRoles(@HandlePage Integer page, @HandleSize Integer size, @RequestHeader("X-Tenant-ID") String tenantId) { 
        var tenantIdAsInt = Integer.parseInt(tenantId);
        var roles = this.organizationRolesService.findAllRoles(page, size, tenantIdAsInt);
        var count = roles.getTotalElements();
        var rolesViews = roles.map((con) -> {
            return con.toViewDTO();
        }).toList();
        
        var res = ApiResponses.GetAllResponse("roles", rolesViews, count, page, size);
        
        return ResponseEntity.ok().body(res);
    }

    @PostMapping("")
    @PreAuthorize("@customSPEL.hasOrgAuthority(authentication, #tenantId, @organizationPermissions.ROLE_CREATE)")
    public ResponseEntity<Object> createRole(@Valid @RequestBody OrganizationRoleCreateDTO dto) {
        var newOrg = this.organizationRolesService.create(dto.toModel());

        var respBody = ApiResponses.OneKey("role", newOrg.toViewDTO());
        return ResponseEntity.status(HttpStatus.CREATED).body(respBody);
    }
        
    @PutMapping("/{id}")
    @PreAuthorize("@customSPEL.hasOrgAuthority(authentication, #tenantId, @organizationPermissions.ROLE_UPDATE)")
    public ResponseEntity<Object> updateRole(@ValidateNumberId @PathVariable Integer id, @Valid @RequestBody OrganizationRoleUpdateDTO dto) {
        var updatedRole = this.organizationRolesService.update(id, dto.toModel());
        
        var respBody = ApiResponses.OneKey("role", updatedRole.toViewDTO());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(respBody);
    }
    

    @DeleteMapping("/{id}")
    @PreAuthorize("@customSPEL.hasOrgAuthority(authentication, #tenantId, @organizationPermissions.ROLE_DELETE)")
    public ResponseEntity<Object> deleteRole(@ValidateNumberId @PathVariable Integer id, @RequestHeader("X-Tenant-ID") String tenantId) {
        this.organizationRolesService.deleteRole(id, Integer.parseInt(tenantId));
        
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/assign")
    @PreAuthorize("@customSPEL.hasOrgAuthority(authentication, #tenantId, @organizationPermissions.ROLE_ASSIGN)")
    public ResponseEntity<Object> assignRole(@Valid @RequestBody AssignOrganizationRoleDTO dto,
        @RequestHeader("X-Tenant-ID") String tenantId
    ) {
        var tenantIdAsInt = Integer.parseInt(tenantId);

        var updatedRole = this.memberShipService.assignRole(dto.getRoleId(),tenantIdAsInt, dto.getUserId());
        var respBody = ApiResponses.OneKey("role", updatedRole.toViewDTO());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(respBody);
    }

    @DeleteMapping("/un-assign/{id}")
    @PreAuthorize("@customSPEL.hasOrgAuthority(authentication, #tenantId, @organizationPermissions.ROLE_UN_ASSIGN)")
    public ResponseEntity<Object> unAssignRole(
        @ValidateNumberId @PathVariable Integer id, 
        @Valid @RequestBody UnAssignOrganizationRoleDTO dto,
        @RequestHeader("X-Tenant-ID") String tenantId
    ) {

        var tenantIdAsInt = Integer.parseInt(tenantId);
        this.memberShipService.unAssignRole(dto.getRoleId(),tenantIdAsInt, dto.getUserId());

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/permissions/assign")
    @PreAuthorize("@customSPEL.hasOrgAuthority(authentication, #tenantId, @organizationPermissions.PERMISSION_UN_ASSIGN)")
    public ResponseEntity<Object> assignPermissions(@Valid @RequestBody AssignOrganizationPermissionsDTO dto,
        @RequestHeader("X-Tenant-ID") String tenantId
    ) {
        var tenantIdAsInt = Integer.parseInt(tenantId);
        var organization = this.organizationsService.findById(tenantIdAsInt);
        if(organization == null) {
            var errRes = ApiResponses.GetErrResponse(String.format("tenant with id: '%s' was not found" ,tenantId));
            return ResponseEntity.badRequest().body(errRes);
        }

        var updatedRole = this.organizationRolesService.assignPermissionsToRole(dto.getRoleId(), dto.getPermissionsIds(), tenantIdAsInt);
        var respBody = ApiResponses.OneKey("role", updatedRole.toViewDTO());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(respBody);
    }

    @DeleteMapping("/permissions/un-assign/{id}")
    @PreAuthorize("@customSPEL.hasOrgAuthority(authentication, #tenantId, @organizationPermissions.PERMISSION_UN_ASSIGN)")
    public ResponseEntity<Object> unAssignPermissions(
        @ValidateNumberId @PathVariable Integer id, 
        @Valid @RequestBody UnAssignOrganizationPermissionsDTO dto,
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
}
