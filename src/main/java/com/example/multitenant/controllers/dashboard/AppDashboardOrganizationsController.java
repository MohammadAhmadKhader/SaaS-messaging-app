package com.example.multitenant.controllers.dashboard;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.multitenant.common.resolvers.contract.HandlePage;
import com.example.multitenant.common.resolvers.contract.HandleSize;
import com.example.multitenant.common.validators.contract.ValidateNumberId;
import com.example.multitenant.dtos.apiResponse.ApiResponses;
import com.example.multitenant.dtos.organizations.OrganizationCreateDTO;
import com.example.multitenant.dtos.organizations.OrganizationUpdateDTO;
import com.example.multitenant.services.contents.ContentsService;
import com.example.multitenant.services.organizations.OrganizationsService;
import com.example.multitenant.services.security.GlobalPermissions;
import com.example.multitenant.services.users.UsersService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

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
import org.springframework.web.bind.annotation.RequestParam;

@Validated
@RestController
@RequestMapping("/api/dashboard/organizations")
public class AppDashboardOrganizationsController {

    private final UsersService usersService;
    private final OrganizationsService organizationsService;

    public AppDashboardOrganizationsController(UsersService usersService, OrganizationsService organizationsService) {
        this.usersService = usersService;
        this.organizationsService = organizationsService;
    }
    
    @GetMapping("")
    @PreAuthorize("hasAuthority(@globalPermissions.DASH_ORGANIZATION_VIEW)")
    public ResponseEntity<Object> getOrganizations(@HandlePage Integer page, @HandleSize Integer size) {

        var orgs = this.organizationsService.findAllOrganization(page, size);
        var count = orgs.getTotalElements();
        var orgsViews = orgs.map((con) -> {
            return con.toViewDTO();
        }).toList();
        
        var res = ApiResponses.GetAllResponse("organizations", orgsViews, count, page, size);
        
        return ResponseEntity.ok().body(res);
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority(@globalPermissions.DASH_ORGANIZATION_CREATE)")
    public ResponseEntity<Object> createOrganization(@Valid @RequestBody OrganizationCreateDTO dto) {

        var newOrg = this.organizationsService.create(dto.toModel());
        var respBody = ApiResponses.OneKey("organization", newOrg.toViewDTO());

        return ResponseEntity.status(HttpStatus.CREATED).body(respBody);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority(@globalPermissions.DASH_ORGANIZATION_UPDATE)")
    public ResponseEntity<Object> updateOrganization(@ValidateNumberId @PathVariable Integer id, @Valid @RequestBody OrganizationUpdateDTO dto) {
        var updatedOrg = this.organizationsService.update(id, dto.toModel());
        var respBody = ApiResponses.OneKey("organization", updatedOrg.toViewDTO());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(respBody);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority(@globalPermissions.DASH_ORGANIZATION_DELETE)")
    public ResponseEntity<Object> deleteOrganization(@ValidateNumberId @PathVariable Integer id) {
        var isDeleted = this.organizationsService.findThenDeleteById(id);
        if(!isDeleted) {
            return ResponseEntity.badRequest().body(ApiResponses.GetNotFoundErr("organization", id));
        }
        
        return ResponseEntity.noContent().build();
    }
}