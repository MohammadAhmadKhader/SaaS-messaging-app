package com.example.multitenant.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.multitenant.common.validators.contract.ValidateNumberId;
import com.example.multitenant.dtos.apiResponse.ApiResponses;
import com.example.multitenant.dtos.categories.*;
import com.example.multitenant.services.categories.CategoriesService;
import com.example.multitenant.utils.AppUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/categories")
public class CategoriesController {
    
    private final CategoriesService categoriesService;
    
    @GetMapping("")
    @PreAuthorize("@customSPEL.hasOrgAuthority(@organizationPermissions.CATEGORY_VIEW)")
    public ResponseEntity<Object> getAllCategories() {
        var tenantId = AppUtils.getTenantId();
        
        var categories = this.categoriesService.findAllWithChannels(tenantId);
        var categoriesViews = categories.stream().map((category) -> {
            return category.toViewDTO();
        }).toList();
        
        var bodyResponse = ApiResponses.OneKey("categories", categoriesViews);
        
        return ResponseEntity.ok(bodyResponse);
    }
    
    @PostMapping("")
    @PreAuthorize("@customSPEL.hasOrgAuthority(@organizationPermissions.CATEGORY_CREATE)")
    public ResponseEntity<Object> createCategory(@Valid @RequestBody CategoryCreateDTO dto) {
        var tenantId = AppUtils.getTenantId();
        
        var category = this.categoriesService.create(dto.toModel(), tenantId);
        var categoryView = category.toViewDTO();

        var responseBody = ApiResponses.OneKey("category", categoryView);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @PatchMapping("/swap-order")
    @PreAuthorize("@customSPEL.hasOrgAuthority(@organizationPermissions.CATEGORY_UPDATE)")
    public ResponseEntity<Void> swapCategoryOrder(@RequestBody CategoryOrderSwapDTO dto) {
        var tenantId = AppUtils.getTenantId();
        this.categoriesService.swapCategoryOrder(dto, tenantId);
        
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("@customSPEL.hasOrgAuthority(@organizationPermissions.CATEGORY_UPDATE)")
    public ResponseEntity<Object> updateCategory(@ValidateNumberId @PathVariable Integer id, @Valid @RequestBody CategoryUpdateDTO dto) {
        var tenantId = AppUtils.getTenantId();
        
        var updatedCategory = this.categoriesService.update(id, dto.toModel(), tenantId);
        if(updatedCategory == null) {
            var respBody = ApiResponses.GetNotFoundErr("category", id);
            return ResponseEntity.badRequest().body(respBody);
        }
        
        var responseBody = ApiResponses.OneKey("category", updatedCategory.toViewDTO());
        
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseBody);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("@customSPEL.hasOrgAuthority(@organizationPermissions.CATEGORY_DELETE)")
    public ResponseEntity<Object> deleteCategory(@ValidateNumberId @PathVariable Integer id) {
        var tenantId = AppUtils.getTenantId();
        this.categoriesService.delete(id, tenantId);
        return ResponseEntity.noContent().build();
    }
}