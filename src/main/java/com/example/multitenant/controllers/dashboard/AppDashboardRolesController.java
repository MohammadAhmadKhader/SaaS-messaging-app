package com.example.multitenant.controllers.dashboard;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.multitenant.common.resolvers.contract.HandlePage;
import com.example.multitenant.common.resolvers.contract.HandleSize;
import com.example.multitenant.dtos.apiResponse.ApiResponses;
import com.example.multitenant.dtos.globalroles.GlobalAssignPermissionsDTO;
import com.example.multitenant.dtos.globalroles.GlobalRoleCreateDTO;
import com.example.multitenant.dtos.globalroles.GlobalRoleUpdateDTO;
import com.example.multitenant.dtos.roles.AssignOrganizationPermissionsDTO;
import com.example.multitenant.services.security.GlobalRolesService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

@Validated
@RestController
@RequestMapping("/api/dashboard/roles")
public class AppDashboardRolesController {

    private final GlobalRolesService globalRolesService;

    public AppDashboardRolesController(GlobalRolesService globalRolesService) {
        this.globalRolesService = globalRolesService;
    }

    @GetMapping("")
    @PreAuthorize("hasAuthority('app-dashboard:role:view')")
    public ResponseEntity<Object> getRoles(@HandlePage Integer page, @HandleSize Integer size) {
            
        var roles = this.globalRolesService.findAllRoles(page, size);
        var count = roles.getTotalElements();
        var rolesViews = roles.map((con) -> {
            return con.toViewDTO();
        }).toList();
        
        var res = ApiResponses.GetAllResponse("roles", rolesViews, count, page, size);
        
        return ResponseEntity.ok().body(res);
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('app-dashboard:role:create')")
    public ResponseEntity<Object> createRole(@Valid @RequestBody GlobalRoleCreateDTO dto) {
        var newRole = this.globalRolesService.create(dto.toModel());

        var respBody = ApiResponses.OneKey("role", newRole.toViewDTO());
        return ResponseEntity.status(HttpStatus.CREATED).body(respBody);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('app-dashboard:role:update')")
    public ResponseEntity<Object> updateRole(@PathVariable Integer id, @Valid @RequestBody GlobalRoleUpdateDTO dto) {
        var updatedRole = this.globalRolesService.update(id, dto.toModel());
        
        var respBody = ApiResponses.OneKey("role", updatedRole.toViewDTO());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(respBody);
    }

    @PostMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('app-dashboard:role:assign')")
    public ResponseEntity<Object> assignPermissions(@PathVariable(name = "id") Integer id,  @Valid @RequestBody AssignOrganizationPermissionsDTO dto) {
        var updatedRole = this.globalRolesService.assignPermissionsToRole(id, dto.getPermissionsIds());
        var respBody = ApiResponses.OneKey("role", updatedRole.toString());
        
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(respBody);
    }

    @PostMapping("/{id}/assign")
    @PreAuthorize("hasAuthority('app-dashboard:role:assign')")
    public ResponseEntity<Object> assignRole(@PathVariable(name = "id") Integer id, @Valid @RequestBody AssignOrganizationPermissionsDTO dto) {
        this.globalRolesService.assignPermissionsToRole(id, dto.getPermissionsIds());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new Object());
    }

    @PostMapping("/{id}/un-assign")
    @PreAuthorize("hasAuthority('app-dashboard:role:un-assign')")
    public ResponseEntity<Object> unAssignRole(@PathVariable(name = "id") Integer id, @Valid @RequestBody GlobalAssignPermissionsDTO dto) {
        this.globalRolesService.unAssignPermissionsFromRole(id, dto.getPermissionsIds());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new Object());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('app-dashboard:role:delete')")
    public ResponseEntity<Object> deleteRole(@PathVariable Integer id) {
        var isDeleted = this.globalRolesService.findThenDeleteById(id);
        if(!isDeleted) {
            return ResponseEntity.badRequest().body(ApiResponses.GetNotFoundErr("role", id));
        }
        
        return ResponseEntity.noContent().build();
    }
}
