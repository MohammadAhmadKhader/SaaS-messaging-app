package com.example.multitenant.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.example.multitenant.common.resolvers.contract.HandlePage;
import com.example.multitenant.common.resolvers.contract.HandleSize;
import com.example.multitenant.common.validators.contract.ValidateNumberId;
import com.example.multitenant.dtos.apiResponse.ApiResponses;
import com.example.multitenant.dtos.users.UserCreateDTO;
import com.example.multitenant.dtos.users.UserUpdateDTO;
import com.example.multitenant.models.enums.DefaultGlobalRole;
import com.example.multitenant.services.users.UsersService;
import com.example.multitenant.utils.FakeDataGenerator;
import com.example.multitenant.utils.SecurityUtils;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/users")
public class UsersController {
    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @PutMapping("")
    public ResponseEntity<Object> updateUserProfile(@Valid @RequestBody UserUpdateDTO dto) {
        var principal = SecurityUtils.getPrincipal();
        if(principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        var userId = principal.getUser().getId();;
        var updatedUser = this.usersService.findThenUpdate(userId, dto.toModel());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(updatedUser.toViewDTO());
    }

    @DeleteMapping("/self-delete")
    public ResponseEntity<Object> selfDeleteUser() {
        var principal = SecurityUtils.getPrincipal();
        if(principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        var user = principal.getUser();
        var isSuperAdmin = user.getRoles().stream().anyMatch((role) -> {
            return role.getName().equals(DefaultGlobalRole.SUPERADMIN.getRoleName());
        });

        if(isSuperAdmin) {
            return ResponseEntity.badRequest().body(ApiResponses.GetErrResponse(String.format("invalid operation, can't delete super admin")));
        }

        var isDeleted = this.usersService.findThenDeleteById(user.getId());
        if(!isDeleted) {
            return ResponseEntity.badRequest().body(ApiResponses.GetNotFoundErr("user", user.getId()));
        }
        
        return ResponseEntity.noContent().build();
    }
}
