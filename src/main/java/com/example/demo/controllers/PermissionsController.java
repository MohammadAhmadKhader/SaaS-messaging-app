package com.example.demo.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dtos.apiResponse.ApiResponses;
import com.example.demo.dtos.permissions.PermissionCreateDTO;
import com.example.demo.dtos.permissions.PermissionUpdateDTO;
import com.example.demo.dtos.roles.RoleCreateDTO;
import com.example.demo.dtos.roles.RoleUpdateDTO;
import com.example.demo.services.security.PermissionsService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/permissions")
public class PermissionsController {
    private final PermissionsService permissionsService;

    public PermissionsController(PermissionsService permissionsService) {
        this.permissionsService = permissionsService;
    }

    @GetMapping("")
    public ResponseEntity<Object> getOrganizations(
        @RequestParam(defaultValue = "1", name = "page") Integer page,
        @RequestParam(defaultValue = "10", name = "size") Integer size ) {
            
        var roles = this.permissionsService.findAllPermissions(page, size);
        var respBody = ApiResponses.GetAllResponse(roles, "permissions");
        
        return ResponseEntity.ok().body(respBody);
    }

    @PostMapping("")
    public ResponseEntity<Object> createRole(@Valid @RequestBody PermissionCreateDTO dto) {
        var newPerm = this.permissionsService.createAndReturnAsView(dto.toModel());

        var respBody = ApiResponses.OneKey("permission", newPerm);
        return ResponseEntity.status(HttpStatus.CREATED).body(respBody);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateRole(@PathVariable Integer id, @Valid @RequestBody PermissionUpdateDTO dto) {
        var updatedPerm = this.permissionsService.updateAndReturnAsView(id, dto.toModel());
        
        var respBody = ApiResponses.OneKey("permission", updatedPerm);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(respBody);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteRole(@PathVariable Integer id) {
        this.permissionsService.deleteById(id);
        
        return ResponseEntity.noContent().build();
    }
}
