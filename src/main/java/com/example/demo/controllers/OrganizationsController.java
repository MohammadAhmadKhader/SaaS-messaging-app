package com.example.demo.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dtos.apiResponse.ApiResponses;
import com.example.demo.dtos.organizations.OrganizationCreateDTO;
import com.example.demo.dtos.organizations.OrganizationUpdateDTO;
import com.example.demo.services.organizations.OrganizationsService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;




@RestController
@RequestMapping("/api/organizations")
public class OrganizationsController {
    
    private final OrganizationsService organizationsService;
    
    public OrganizationsController(OrganizationsService organizationsService) {
        this.organizationsService = organizationsService;
    }

    @GetMapping("")
    @PreAuthorize("hasAuthority('view:organization')")
    public ResponseEntity<Object> getOrganizations(
        @RequestParam(defaultValue = "1", name = "page") Integer page,
        @RequestParam(defaultValue = "10", name = "size") Integer size ) {
        var orgs = this.organizationsService.findAllOrganization(page, size);

        var respBody = ApiResponses.GetAllResponse(orgs, "organizations");
        
        return ResponseEntity.ok().body(respBody);
    }

    @PostMapping("")
    public ResponseEntity<Object> createOrganization(@Valid @RequestBody OrganizationCreateDTO dto) {
        var newOrg = this.organizationsService.createAndReturnAsView(dto.toModel());

        var respBody = ApiResponses.OneKey("organization", newOrg);
        return ResponseEntity.status(HttpStatus.CREATED).body(respBody);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateOrganization(@PathVariable Integer id, @Valid @RequestBody OrganizationUpdateDTO dto) {
        var updatedOrg = this.organizationsService.updateAndReturnAsView(id, dto.toModel());
        
        var respBody = ApiResponses.OneKey("organization", updatedOrg);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(respBody);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteOrganization(@PathVariable Integer id) {
        this.organizationsService.deleteById(id);
        
        return ResponseEntity.noContent().build();
    }
    
}
