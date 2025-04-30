package com.example.multitenant.controllers.organizationdashboard;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.example.multitenant.common.resolvers.contract.*;
import com.example.multitenant.common.validators.contract.ValidateNumberId;
import com.example.multitenant.dtos.apiresponse.ApiResponses;
import com.example.multitenant.dtos.organizationroles.*;
import com.example.multitenant.models.enums.LogEventType;
import com.example.multitenant.services.cache.*;
import com.example.multitenant.services.logs.LogsService;
import com.example.multitenant.services.membership.MemberShipService;
import com.example.multitenant.services.organizations.OrganizationsService;
import com.example.multitenant.services.security.OrganizationRolesService;
import com.example.multitenant.utils.AppUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/organizations/dashboard/roles")
public class OrganizationDashboardRolesController {

    private final OrganizationsService organizationsService;
    private final OrganizationRolesService organizationRolesService;
    private final MemberShipService memberShipService;
    private final RedisService redisService;
    private final LogsService logsService;

    @GetMapping("")
    @PreAuthorize("@customSPEL.hasOrgAuthority(@organizationPermissions.ROLE_VIEW)")
    public ResponseEntity<Object> getRoles(@HandlePage Integer page, @HandleSize Integer size) { 
        var tenantId = AppUtils.getTenantId();

        var roles = this.organizationRolesService.findAllRoles(page, size, tenantId);
        var count = roles.getTotalElements();
        var rolesViews = roles.map((con) -> {
            return con.toViewDTO();
        }).toList();
        
        var res = ApiResponses.GetAllResponse("roles", rolesViews, count, page, size);
        
        return ResponseEntity.ok().body(res);
    }

    @PostMapping("")
    @PreAuthorize("@customSPEL.hasOrgAuthority(@organizationPermissions.ROLE_CREATE)")
    public ResponseEntity<Object> createRole(@Valid @RequestBody OrganizationRoleCreateDTO dto) {
        var orgId = AppUtils.getTenantId();
        var orgRole = this.organizationRolesService.findByNameAndOrganizationId(dto.getName(), orgId);
        if(orgRole != null) {
            return ResponseEntity.badRequest().body(ApiResponses.GetErrResponse("role with such name already exists"));
        }

        var newOrgRole = this.organizationRolesService.createWithBasicPerms(dto.toModel(), orgId);
        this.redisService.invalidateOrgRolesCache(orgId);

        var respBody = ApiResponses.OneKey("role", newOrgRole.toViewDTO());

        return ResponseEntity.status(HttpStatus.CREATED).body(respBody);
    }
        
    @PutMapping("/{id}")
    @PreAuthorize("@customSPEL.hasOrgAuthority(@organizationPermissions.ROLE_UPDATE)")
    public ResponseEntity<Object> updateRole(@ValidateNumberId @PathVariable Integer id, @Valid @RequestBody OrganizationRoleUpdateDTO dto) {
        var orgId = AppUtils.getTenantId();
        var updatedRole = this.organizationRolesService.findThenUpdate(id, orgId, dto.toModel());
        if(updatedRole == null) {
            return ResponseEntity.badRequest().body(ApiResponses.GetNotFoundErr("role", id));
        }

        this.redisService.invalidateOrgRolesCache(orgId);
        var respBody = ApiResponses.OneKey("role", updatedRole.toViewDTO());
        
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(respBody);
    }
    

    @DeleteMapping("/{id}")
    @PreAuthorize("@customSPEL.hasOrgAuthority(@organizationPermissions.ROLE_DELETE)")
    public ResponseEntity<Object> deleteRole(@ValidateNumberId @PathVariable Integer id) {
        var tenantId = AppUtils.getTenantId();
        
        this.organizationRolesService.deleteRole(id, tenantId);
        this.redisService.handleRoleDeletionInvalidations(tenantId, id);
        
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/assign")
    @PreAuthorize("@customSPEL.hasOrgAuthority(@organizationPermissions.ROLE_ASSIGN)")
    public ResponseEntity<Object> assignRole(@Valid @RequestBody AssignOrganizationRoleDTO dto) {  
        var tenantId = AppUtils.getTenantId();
        var user = AppUtils.getUserFromAuth();

        var orgRole = this.memberShipService.assignRole(dto.getRoleId(), tenantId, dto.getUserId());
        this.redisService.invalidateUserOrgRolesCache(tenantId, dto.getUserId());
        this.logsService.createRolesAssignmentsLog(user, orgRole, dto.getUserId(), tenantId, LogEventType.ROLE_ASSIGN);

        var respBody = ApiResponses.OneKey("role", orgRole.toViewDTO());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(respBody);
    }

    @DeleteMapping("/un-assign/{id}/{userId}")
    @PreAuthorize("@customSPEL.hasOrgAuthority(@organizationPermissions.ROLE_UN_ASSIGN)")
    public ResponseEntity<Object> unAssignRole(@ValidateNumberId @PathVariable Integer id, @ValidateNumberId @PathVariable Long userId) {
        var tenantId = AppUtils.getTenantId();
        var user = AppUtils.getUserFromAuth();

        var orgRole = this.memberShipService.unAssignRole(id, tenantId, userId);
        this.redisService.invalidateUserOrgRolesCache(tenantId, userId);
        this.logsService.createRolesAssignmentsLog(user, orgRole, userId, tenantId, LogEventType.ROLE_UNASSIGN);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/permissions/assign")
    @PreAuthorize("@customSPEL.hasOrgAuthority(@organizationPermissions.PERMISSION_UN_ASSIGN)")
    public ResponseEntity<Object> assignPermissions(@Valid @RequestBody AssignOrganizationPermissionsDTO dto) {
        var tenantId = AppUtils.getTenantId();
        var organization = this.organizationsService.findById(tenantId);
        if(organization == null) {
            var errRes = ApiResponses.GetErrResponse(String.format("tenant with id: '%s' was not found" ,tenantId));
            return ResponseEntity.badRequest().body(errRes);
        }

        var updatedRole = this.organizationRolesService.assignPermissionsToRole(dto.getRoleId(), dto.getPermissionsIds(), tenantId);
        this.redisService.invalidateOrgRolesCache(tenantId);

        var respBody = ApiResponses.OneKey("role", updatedRole.toViewDTO());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(respBody);
    }

    @DeleteMapping("/permissions/un-assign/{id}")
    @PreAuthorize("@customSPEL.hasOrgAuthority(@organizationPermissions.PERMISSION_UN_ASSIGN)")
    public ResponseEntity<Object> unAssignPermissions(@ValidateNumberId @PathVariable Integer id, @Valid @RequestBody UnAssignOrganizationPermissionsDTO dto) {
        var tenantId = AppUtils.getTenantId();
        var organization = this.organizationsService.findById(tenantId);
        if(organization == null) {
            var errRes = ApiResponses.GetErrResponse(String.format("tenant with id: '%s' was not found" ,tenantId));
            return ResponseEntity.badRequest().body(errRes);
        }

        this.organizationRolesService.unAssignPermissionsFromRole(id, dto.getPermissionsIds(), tenantId);
        this.redisService.invalidateOrgRolesCache(tenantId);

        return ResponseEntity.noContent().build();
    }
}
