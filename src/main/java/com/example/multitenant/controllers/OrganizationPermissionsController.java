package com.example.multitenant.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.multitenant.common.validators.contract.ValidateNumberId;
import com.example.multitenant.dtos.apiResponse.ApiResponses;
import com.example.multitenant.dtos.organizationpermissions.OrganizationPermissionCreateDTO;
import com.example.multitenant.dtos.organizationpermissions.OrganizationPermissionUpdateDTO;
import com.example.multitenant.services.security.OrganizationPermissionsService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

@Validated
@RestController
@RequestMapping("/api/permissions")
public class OrganizationPermissionsController {
    private final OrganizationPermissionsService organizationPermissionsService;

    public OrganizationPermissionsController(OrganizationPermissionsService organizationPermissionsService) {
        this.organizationPermissionsService = organizationPermissionsService;
    }

    @GetMapping("")
    public ResponseEntity<Object> getPermissions(
        @RequestParam(defaultValue = "1") @Min(value = 1 ,message = "page must be at least {value}") Integer page,
        @RequestParam(defaultValue = "10") @Min(4) Integer size) {
            
        var permissions = this.organizationPermissionsService.findAllPermissions(page, size);
        var count = permissions.getTotalElements();
        var permissionsViews = permissions.map((con) -> {
            return con.toViewDTO();
        }).toList();
        
        var res = ApiResponses.GetAllResponse("permissions", permissionsViews, count, page, size);
        
        return ResponseEntity.ok().body(res);
    }

    @PostMapping("")
    public ResponseEntity<Object> createRole(@Valid @RequestBody OrganizationPermissionCreateDTO dto) {
        var newPerm = this.organizationPermissionsService.create(dto.toModel());

        var respBody = ApiResponses.OneKey("permission", newPerm.toViewDTO());
        return ResponseEntity.status(HttpStatus.CREATED).body(respBody);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateRole(@ValidateNumberId @PathVariable Integer id, @Valid @RequestBody OrganizationPermissionUpdateDTO dto) {
        var updatedPerm = this.organizationPermissionsService.update(id, dto.toModel());
        
        var respBody = ApiResponses.OneKey("permission", updatedPerm.toViewDTO());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(respBody);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteRole(@ValidateNumberId @PathVariable Integer id) {
        var isDeleted = this.organizationPermissionsService.findThenDeleteById(id);
        if(!isDeleted) {
            return ResponseEntity.badRequest().body(ApiResponses.GetNotFoundErr("role", id));
        }
        
        return ResponseEntity.noContent().build();
    }
}
