package com.example.demo.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dtos.apiResponse.ApiResponses;
import com.example.demo.dtos.organizations.OrganizationCreateDTO;
import com.example.demo.dtos.organizations.OrganizationUpdateDTO;
import com.example.demo.dtos.roles.AssignPermissionsDTO;
import com.example.demo.dtos.roles.RoleCreateDTO;
import com.example.demo.dtos.roles.RoleUpdateDTO;
import com.example.demo.services.organizations.OrganizationsService;
import com.example.demo.services.security.RolesService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/roles")
public class RolesController {
    private final RolesService rolesService;

    public RolesController(RolesService rolesService) {
        this.rolesService = rolesService;
    }

    @GetMapping("")
    public ResponseEntity<Object> getOrganizations(
        @RequestParam(defaultValue = "1", name = "page") Integer page,
        @RequestParam(defaultValue = "10", name = "size") Integer size ) {
            
        var roles = this.rolesService.findAllRoles(page, size);
        var respBody = ApiResponses.GetAllResponse(roles, "roles");
        
        return ResponseEntity.ok().body(respBody);
    }

    @PostMapping("")
    public ResponseEntity<Object> createRole(@Valid @RequestBody RoleCreateDTO dto) {
        var newOrg = this.rolesService.createAndReturnAsView(dto.toModel());

        var respBody = ApiResponses.OneKey("role", newOrg);
        return ResponseEntity.status(HttpStatus.CREATED).body(respBody);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateRole(@PathVariable Integer id, @Valid @RequestBody RoleUpdateDTO dto) {
        var updatedRole = this.rolesService.updateAndReturnAsView(id, dto.toModel());
        
        var respBody = ApiResponses.OneKey("role", updatedRole);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(respBody);
    }

    @PostMapping("/{id}/permissions")
    public ResponseEntity<Object> assignPermissions(@PathVariable(name = "id") Integer id, @Valid @RequestBody AssignPermissionsDTO dto) {
        var updatedRole = this.rolesService.assignPermissionsToRole(id, dto.getPermissionsIds());
        var respBody = ApiResponses.OneKey("role", updatedRole);
        
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(respBody);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteRole(@PathVariable Integer id) {
        this.rolesService.deleteById(id);
        
        return ResponseEntity.noContent().build();
    }
}
