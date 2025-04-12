package com.example.demo.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.example.demo.common.resolvers.contract.HandlePage;
import com.example.demo.common.resolvers.contract.HandleSize;
import com.example.demo.common.validators.contract.ValidateNumberId;
import com.example.demo.dtos.apiResponse.ApiResponses;
import com.example.demo.dtos.users.UserCreateDTO;
import com.example.demo.services.users.UsersService;
import com.example.demo.utils.FakeDataGenerator;

import jakarta.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("")
    public ResponseEntity<Object> getAllUsers(
    @HandlePage Integer page, @HandleSize Integer size, 
    @RequestParam(defaultValue = "createdAt") String sortBy, @RequestParam(defaultValue = "DESC") String sortDir) {
        
        var users = this.usersService.findAllUsers(page, size, sortBy, sortDir);
        var usersView = users.map((user) -> {
            return user.toViewDTO();
        }).toList();
        var count = users.getTotalElements();

        var respBody = ApiResponses.GetAllResponse("users", usersView, count, page, size);

        return ResponseEntity.ok().body(respBody);
    }

    @GetMapping("/contents")
    public ResponseEntity<Object> getAllUsersWithContents(@HandlePage Integer page, @HandleSize Integer size,
        @RequestParam(defaultValue = "createdAt") String sortBy, @RequestParam(defaultValue = "DESC") String sortDir
    ) {
        var users = this.usersService.findAllWithContents(page, size, sortBy, sortDir);
        var count = users.getTotalElements();
        var usersView = users.map((user) -> {
            return user.toViewDTO();
        }).toList();

        var res = ApiResponses.GetAllResponse("users", usersView, count, page, size);

        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable Long id) {
        System.out.println("recieved the following id: " + id);
        var user = this.usersService.findById(id);
        
        var respBody = ApiResponses.OneKey("user", user.toViewDTO());
        return ResponseEntity.ok().body(respBody);
    }

    @PostMapping("")
    public ResponseEntity<Object> createUser(@RequestBody UserCreateDTO dto) {
        var createdUser = this.usersService.create(dto.toModel());
        var respBody = ApiResponses.OneKey("user", createdUser.toViewDTO());

        return ResponseEntity.status(HttpStatus.CREATED).body(respBody);
    }
}
