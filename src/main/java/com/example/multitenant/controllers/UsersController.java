package com.example.multitenant.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.example.multitenant.common.resolvers.contract.HandlePage;
import com.example.multitenant.common.resolvers.contract.HandleSize;
import com.example.multitenant.common.validators.contract.ValidateNumberId;
import com.example.multitenant.dtos.apiResponse.ApiResponses;
import com.example.multitenant.dtos.users.UserCreateDTO;
import com.example.multitenant.services.users.UsersService;
import com.example.multitenant.utils.FakeDataGenerator;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Validated
@RestController
@RequestMapping("/api/users")
public class UsersController {
    
    @Autowired
    private UsersService usersService;
}
