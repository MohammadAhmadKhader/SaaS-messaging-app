package com.example.multitenant.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.multitenant.common.annotations.contract.AuthorizeOrg;
import com.example.multitenant.common.resolvers.contract.HandlePage;
import com.example.multitenant.common.resolvers.contract.HandleSize;
import com.example.multitenant.common.validators.contract.ValidateNumberId;
import com.example.multitenant.common.validators.contract.ValidateSize;
import com.example.multitenant.dtos.apiResponse.ApiResponses;
import com.example.multitenant.dtos.contents.ContentCreateDTO;
import com.example.multitenant.dtos.contents.ContentUpdateDTO;
import com.example.multitenant.services.contents.ContentsService;
import com.example.multitenant.services.security.OrganizationPermissions;
import com.example.multitenant.services.users.UsersService;
import com.example.multitenant.utils.AppUtils;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/contents")
public class ContentsController {

    private final ContentsService contentsService;
    private final UsersService usersService;

    @GetMapping("")
    @PreAuthorize("@customSPEL.hasOrgAuthority(@organizationPermissions.CONTENT_VIEW)")
    public ResponseEntity<Object> getAllContents(@HandlePage Integer page, @HandleSize Integer size,
        @RequestParam(defaultValue = "createdAt") String sortBy, @RequestParam(defaultValue = "DESC") String sortDir, 
        @RequestParam(defaultValue = "") List<String> filters) {
        var tenantId = AppUtils.getTenantId();

        var contents = this.contentsService.findAllPopulatedWithFilters(page, size, sortBy, sortBy, filters, tenantId);
        var count = contents.getTotalElements();
        var contentsViews = contents.map((con) -> {
            return con.toViewDTO();
        }).toList();

        var bodyResponse = ApiResponses.GetAllResponse("contents", contentsViews, count, page, size);

        return ResponseEntity.ok(bodyResponse);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@customSPEL.hasOrgAuthority(@organizationPermissions.CONTENT_VIEW)")
    public ResponseEntity<Object> getContentById(@PathVariable @ValidateNumberId Integer id) {
        var tenantId = AppUtils.getTenantId();

        var content = this.contentsService.findByIdAndOrganizationId(id, tenantId);
        if(content == null) {
            var respBody = ApiResponses.GetErrResponse(String.format("content with id: %s was not found", id));
            return ResponseEntity.badRequest().body(respBody);
        }

        var bodyResponse = ApiResponses.OneKey("content",content.toViewDTO());

        return ResponseEntity.ok(bodyResponse);
    }

    @PostMapping("")
    @PreAuthorize("@customSPEL.hasOrgAuthority(@organizationPermissions.CONTENT_CREATE)")
    public ResponseEntity<Object> createContent(@Valid @RequestBody ContentCreateDTO dto) {
        var tenantId = AppUtils.getTenantId();

        var content = this.contentsService.createByUser(dto.toModel(), tenantId);
        var contentView = content.toViewDTO();
        var responseBody = ApiResponses.OneKey("content", contentView);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@customSPEL.hasOrgAuthority(@organizationPermissions.CONTENT_UPDATE)")
    public ResponseEntity<Object> updateContent(@ValidateNumberId @PathVariable Integer id, @Valid @RequestBody ContentUpdateDTO dto) {
        var tenantId = AppUtils.getTenantId();

        var updatedContent = this.contentsService.updateByUser(id, dto.toModel(), tenantId);
        if(updatedContent == null) {
            var respBody = ApiResponses.GetErrResponse(String.format("content with id: '%s' does not exist", id));
            return ResponseEntity.badRequest().body(respBody);
        } 

        var responseBody = ApiResponses.OneKey("content", updatedContent.toViewDTO());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseBody);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@customSPEL.hasOrgAuthority(@organizationPermissions.CONTENT_DELETE)")
    public ResponseEntity<Object> deleteContent(@ValidateNumberId @PathVariable Integer id) {
        var tenantId = AppUtils.getTenantId();
        this.contentsService.deleteByUser(id, tenantId);
        return ResponseEntity.noContent().build();
    }
    
}
