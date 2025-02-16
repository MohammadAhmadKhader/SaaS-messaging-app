package com.example.demo.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dtos.apiResponse.ApiResponses;
import com.example.demo.dtos.users.UserCreateDTO;
import com.example.demo.services.users.UsersService;
import com.example.demo.utils.FakeDataGenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/users")
public class UsersController {
    
    @Autowired
    private UsersService usersService;

    @GetMapping("")
    public ResponseEntity<Object> getAllUsers(
        @RequestParam(name = "page", defaultValue = "1") Integer page, @RequestParam(name = "size", defaultValue = "10") Integer size,
        @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy, @RequestParam(name = "sortDir", defaultValue = "DESC") String sortDir
        ) {
        
        var result = usersService.findAllUsers(page, size, sortBy, sortDir);
        var respBody = ApiResponses.GetAllResponse(result, "users");

        return ResponseEntity.ok().body(respBody);
    }

    @GetMapping("/contents")
    public ResponseEntity<Object> getAllUsersWithContents(
        @RequestParam(name = "page", defaultValue = "1") Integer page, @RequestParam(name = "size", defaultValue = "10") Integer size,
        @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy, @RequestParam(name = "sortDir", defaultValue = "DESC") String sortDir
    ) {

        var result = usersService.findAllWithContents(page, size, sortBy, sortDir);
        var respBody = ApiResponses.GetAllResponse(result, "users");

        return ResponseEntity.ok().body(respBody);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable Long id) {
        var user = usersService.findByIdAsView(id);
        
        var respBody = ApiResponses.OneKey("user", user);
        return ResponseEntity.ok().body(respBody);
    }

    @PostMapping("")
    public ResponseEntity<Object> createUser(@RequestBody UserCreateDTO dto) {
        var createdUser = usersService.createAndReturnAsView(dto.toModel());
        var respBody = ApiResponses.OneKey("user", createdUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(respBody);
    }

    @PostMapping("fake-data")
    public void generateUserData() {
        var usersNumber = 100;
        
        for(var i = 0; i < usersNumber; i ++) {
            var fakeUser = FakeDataGenerator.generateFakeUser();
            System.out.println("Generated Fake User:");
            System.out.println("Name: " + fakeUser.getFirstName());
            System.out.println("Email: " + fakeUser.getEmail());
            System.out.println("Password: " + fakeUser.getPassword());
            usersService.create(fakeUser);
        }
        
    }
    
}
