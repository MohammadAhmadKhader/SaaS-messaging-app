package com.example.multitenant.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.multitenant.common.annotations.contract.*;
import com.example.multitenant.common.validators.contract.ValidateNumberId;
import com.example.multitenant.dtos.apiresponse.ApiResponses;
import com.example.multitenant.dtos.categories.*;
import com.example.multitenant.models.enums.*;
import com.example.multitenant.services.cache.CategoriesCacheService;
import com.example.multitenant.services.cache.SubscriptionLimitChecker;
import com.example.multitenant.services.categories.CategoriesService;
import com.example.multitenant.services.logs.LogsService;
import com.example.multitenant.services.membership.MemberShipService;
import com.example.multitenant.utils.AppUtils;
import com.example.multitenant.utils.SecurityUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@CheckRestricted
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/categories")
public class CategoriesController {
    
    private final CategoriesService categoriesService;
    private final CategoriesCacheService categoriesCacheService;
    private final LogsService logsService;
    private final SubscriptionLimitChecker subscriptionLimitChecker;

    @GetMapping("")
    @PreAuthorize("@customSPEL.hasOrgAuthority(@orgPermissions.CATEGORY_VIEW)")
    public ResponseEntity<Object> getAllCategories() {
        var tenantId = AppUtils.getTenantId();
        var user = SecurityUtils.getUserFromAuth();
        
        var filteredCategoriesViews = this.categoriesCacheService.getCategories(tenantId, user.getId());
        var bodyResponse = ApiResponses.OneKey("categories", filteredCategoriesViews);
        
        return ResponseEntity.ok(bodyResponse);
    }
    
    @PostMapping("")
    @PreAuthorize("@customSPEL.hasOrgAuthority(@orgPermissions.CATEGORY_CREATE)")
    @ValidateSubscriptionLimit(limit = StripeLimit.CATEGORIES, counterOperation = StripeCounterOperation.INCREMENT)
    public ResponseEntity<Object> createCategory(@Valid @RequestBody CategoryCreateDTO dto) {
        var tenantId = AppUtils.getTenantId();
        var user = SecurityUtils.getUserFromAuth();

        var category = this.categoriesService.create(dto.toModel(), tenantId);
        this.categoriesCacheService.invalidateOrgCategoriesUserRoles(tenantId, "*");
        this.logsService.createCategoriesLog(user, category, tenantId, LogEventType.ORG_CATEGORY_CREATED);

        var categoryView = category.toViewDTO();
        var responseBody = ApiResponses.OneKey("category", categoryView);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @PatchMapping("/swap-order")
    @PreAuthorize("@customSPEL.hasOrgAuthority(@orgPermissions.CATEGORY_UPDATE)")
    @TenantHandlerLocker
    public ResponseEntity<Void> swapCategoryOrder(@RequestBody CategoryOrderSwapDTO dto) {
        var tenantId = AppUtils.getTenantId();
        
        this.categoriesService.swapCategoryOrder(dto, tenantId);
        this.categoriesCacheService.invalidateOrgCategoriesUserRoles(tenantId, "*");
        this.categoriesCacheService.invalidateOrgCategories(tenantId, dto.getCategoryId1());
        this.categoriesCacheService.invalidateOrgCategories(tenantId, dto.getCategoryId2());
        
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("@customSPEL.hasOrgAuthority(@orgPermissions.CATEGORY_UPDATE)")
    public ResponseEntity<Object> updateCategory(@ValidateNumberId @PathVariable Integer id, @Valid @RequestBody CategoryUpdateDTO dto) {
        var tenantId = AppUtils.getTenantId();
        var user = SecurityUtils.getUserFromAuth();
        
        var updatedCategory = this.categoriesService.update(id, dto.toModel(), tenantId);
        if(updatedCategory == null) {
            var respBody = ApiResponses.GetNotFoundErr("category", id);
            return ResponseEntity.badRequest().body(respBody);
        }
        
        var responseBody = ApiResponses.OneKey("category", updatedCategory.toViewDTO());
        this.categoriesCacheService.invalidateOrgCategories(tenantId, id);
        this.categoriesCacheService.invalidateOrgCategoriesUserRoles(tenantId, "*");
        this.logsService.createCategoriesLog(user, updatedCategory, tenantId, LogEventType.ORG_CATEGORY_UPDATED);
        
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseBody);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("@customSPEL.hasOrgAuthority(@orgPermissions.CATEGORY_DELETE)")
    public ResponseEntity<Object> deleteCategory(@ValidateNumberId @PathVariable Integer id) {
        var tenantId = AppUtils.getTenantId();

        this.categoriesService.delete(id, tenantId);
        this.categoriesCacheService.invalidateOrgCategories(tenantId, id);
        this.categoriesCacheService.invalidateOrgCategoriesUserRoles(tenantId, "*");

        this.subscriptionLimitChecker.decrementOrgCategoriesCount(tenantId);

        return ResponseEntity.noContent().build();
    }
}