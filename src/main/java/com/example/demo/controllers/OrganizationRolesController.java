package com.example.demo.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.common.resolvers.contract.HandlePage;
import com.example.demo.common.resolvers.contract.HandleSize;
import com.example.demo.common.validators.contract.ValidateNumberId;
import com.example.demo.dtos.apiResponse.ApiResponses;
import com.example.demo.dtos.organizations.OrganizationCreateDTO;
import com.example.demo.dtos.organizations.OrganizationUpdateDTO;
import com.example.demo.dtos.roles.AssignOrganizationPermissionsDTO;
import com.example.demo.dtos.roles.OrganizationRoleCreateDTO;
import com.example.demo.dtos.roles.OrganizationRoleUpdateDTO;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.services.organizations.OrganizationsService;
import com.example.demo.services.security.OrganizationRolesService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

@Validated
@RestController
@RequestMapping("/api/roles")
public class OrganizationRolesController {
    private final OrganizationsService organizationsService;
    private final OrganizationRolesService organizationRolesService;

    public OrganizationRolesController(OrganizationRolesService organizationRolesService ,OrganizationsService organizationsService) {
        this.organizationRolesService = organizationRolesService;
        this.organizationsService = organizationsService;
    }
    
    @GetMapping("")
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
    public ResponseEntity<Object> createRole(@Valid @RequestBody OrganizationRoleCreateDTO dto) {
        var newOrg = this.organizationRolesService.create(dto.toModel());

        var respBody = ApiResponses.OneKey("role", newOrg.toViewDTO());
        return ResponseEntity.status(HttpStatus.CREATED).body(respBody);
    }
        
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateRole(@ValidateNumberId @PathVariable Integer id, @Valid @RequestBody OrganizationRoleUpdateDTO dto) {
        var updatedRole = this.organizationRolesService.update(id, dto.toModel());
        
        var respBody = ApiResponses.OneKey("role", updatedRole.toViewDTO());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(respBody);
    }
    
    @PostMapping("/{id}/permissions")
    public ResponseEntity<Object> assignPermissions(@ValidateNumberId @PathVariable(name = "id") Integer id, @Valid @RequestBody AssignOrganizationPermissionsDTO dto,
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteRole(@ValidateNumberId @PathVariable Integer id, @RequestHeader("X-Tenant-ID") String tenantId) {
        this.organizationRolesService.deleteRole(id, Integer.parseInt(tenantId));
        
        return ResponseEntity.noContent().build();
    }
}
