package com.example.multitenant.controllers.dashboard;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.multitenant.common.resolvers.contract.*;
import com.example.multitenant.common.validators.contract.ValidateNumberId;
import com.example.multitenant.dtos.apiResponse.ApiResponses;
import com.example.multitenant.dtos.organizations.*;
import com.example.multitenant.models.Membership;
import com.example.multitenant.models.Organization;
import com.example.multitenant.models.binders.MembershipKey;
import com.example.multitenant.services.contents.ContentsService;
import com.example.multitenant.services.membership.MemberShipService;
import com.example.multitenant.services.organizations.OrganizationsService;
import com.example.multitenant.services.security.GlobalPermissions;
import com.example.multitenant.services.users.UsersService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import java.util.Map;

import org.hibernate.Hibernate;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/dashboard/organizations")
public class AppDashboardOrganizationsController {

    private final UsersService usersService;
    private final OrganizationsService organizationsService;
    private final MemberShipService memberShipService;
    
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

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority(@globalPermissions.DASH_ORGANIZATION_VIEW)")
    public ResponseEntity<Object> getOrganizationWithUsersById(@HandlePage Integer page, @HandleSize Integer size, @PathVariable(name = "id") Integer organizationId) {
        var memberships = this.memberShipService.getOrganizaionMemberships(page, size, organizationId);
        var count = memberships.getTotalElements();
        System.out.println(memberships);
        
        var firstMemberShip = memberships.getContent().get(0);
        if(firstMemberShip == null) {
            return ResponseEntity.badRequest().body(ApiResponses.GetNotFoundErr("organization with id '%s' was not found", organizationId));
        }

        var org = firstMemberShip.getOrganization();
        var users = memberships.map((m) -> m.getUser().toViewWithoutRolesDTO()).toList();

        var orgView = new OrganizationWithUserRolesViewDTO(org);
        orgView.setUsers(users);

        var res = ApiResponses.GetNestedAllResponse("organization", orgView, count, page, size);
        
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
        var updatedOrg = this.organizationsService.findThenUpdate(id, dto.toModel());
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