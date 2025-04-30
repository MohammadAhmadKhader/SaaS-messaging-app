package com.example.multitenant.controllers.dashboard;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.multitenant.common.resolvers.contract.*;
import com.example.multitenant.common.validators.contract.ValidateNumberId;
import com.example.multitenant.dtos.apiResponse.ApiResponses;
import com.example.multitenant.dtos.users.UserCreateDTO;
import com.example.multitenant.models.enums.DefaultGlobalRole;
import com.example.multitenant.services.users.UsersService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/dashboard/users")
public class AppDashboardUsersController {
    
    @Autowired
    private UsersService usersService;

    @GetMapping("/test")
    @PreAuthorize("hasAuthority(@globalPermissions.DASH_USER_VIEW)")
    public ResponseEntity<Object> test() {
        var res = Map.of("message","hello world");
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("")
    @PreAuthorize("hasAuthority(@globalPermissions.DASH_USER_VIEW)")
    public ResponseEntity<Object> getAllUsers(@HandlePage Integer page, @HandleSize Integer size, 
    @RequestParam(defaultValue = "createdAt") String sortBy, @RequestParam(defaultValue = "DESC") String sortDir) {
        
        var users = this.usersService.findAllUsers(page, size, sortBy, sortDir);
        var usersView = users.map((user) -> {
            return user.toViewDTO();
        }).toList();
        var count = users.getTotalElements();

        var respBody = ApiResponses.GetAllResponse("users", usersView, count, page, size);

        return ResponseEntity.ok().body(respBody);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority(@globalPermissions.DASH_USER_VIEW)")
    public ResponseEntity<Object> getUser(@ValidateNumberId @PathVariable Long id) {
        var user = this.usersService.findById(id);
        if(user == null) {
            return ResponseEntity.badRequest().body(ApiResponses.GetNotFoundErr("user", id));
        }
        
        var respBody = ApiResponses.OneKey("user", user.toViewDTO());
        return ResponseEntity.ok().body(respBody);
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority(@globalPermissions.DASH_USER_CREATE)")
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserCreateDTO dto) {
        var createdUser = this.usersService.create(dto.toModel());
        var respBody = ApiResponses.OneKey("user", createdUser.toViewDTO());

        return ResponseEntity.status(HttpStatus.CREATED).body(respBody);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority(@globalPermissions.DASH_USER_DELETE)")
    public ResponseEntity<Object> deleteUser(@ValidateNumberId @PathVariable Long id) {
        var user = this.usersService.findById(id);
        if(user == null) {
            return ResponseEntity.badRequest().body(ApiResponses.GetNotFoundErr("user", id));
        }

        var isAdmin = user.getRoles().stream().anyMatch((role) -> {
            return role.getName().equals(DefaultGlobalRole.SUPERADMIN.getRoleName()) || role.getName().equals(DefaultGlobalRole.ADMIN.getRoleName());
        });

        if(isAdmin) {
            return ResponseEntity.badRequest().body(ApiResponses.GetErrResponse("invalid operation"));
        }
        
        return ResponseEntity.noContent().build();
    }
}
