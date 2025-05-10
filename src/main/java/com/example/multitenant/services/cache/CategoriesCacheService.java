package com.example.multitenant.services.cache;

import java.io.Serializable;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.multitenant.common.annotations.contract.LogMethod;
import com.example.multitenant.dtos.categories.CategoryViewDTO;
import com.example.multitenant.dtos.organizationroles.OrgRoleCacheDTO;
import com.example.multitenant.exceptions.ResourceNotFoundException;
import com.example.multitenant.models.Category;
import com.example.multitenant.services.categories.CategoriesService;
import com.example.multitenant.services.membership.MemberShipService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RequiredArgsConstructor
@Service
public class CategoriesCacheService {
    private final MemberShipService memberShipService;
    private final CategoriesService categoriesService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final AuthCacheService authCacheService;

    private static final Duration CACHE_TTL = Duration.ofMinutes(30);

    @LogMethod()
    @SuppressWarnings("unchecked")
    public List<CategoryViewDTO> getCategories(Integer orgId, Long userId) {
        log.info("getting categories");
        var userOrgRoles = this.authCacheService.getUserOrgRoles(orgId, userId);
        var userOrgRolesIds = userOrgRoles.stream().map((role) -> role.getId()).toList();

        var key = this.getOrgCategoriesUserRolesCacheKey(orgId, userOrgRolesIds.toString());
        var cached = this.redisTemplate.opsForValue().get(key);

        if(cached == null) {
            return this.fetchCategoriesAndCache(orgId, userId, userOrgRolesIds);
        }

        return (List<CategoryViewDTO>) cached;
    }

    @LogMethod
    public CategoryViewDTO getCategory(Integer orgId, Integer categoryId) {
        var key = this.getOrgCategoriesCacheKey(orgId, categoryId);
        var cached = this.redisTemplate.opsForValue().get(key);
        if(cached == null) {
            return this.fetchCategory(orgId, categoryId);
        }

        return (CategoryViewDTO) cached;
    }

    private List<CategoryViewDTO> fetchCategoriesAndCache(Integer orgId, long userId, List<Integer> userRolesIds) {
        var categories = this.categoriesService.findAllWithChannelsAndRoles(orgId);
        var membership = this.memberShipService.findUserMembershipWithRoles(orgId, userId);
        
        var filteredCategoriesView = categories.stream().filter((cat) -> {
            return cat.getAuthorizedRoles().stream().anyMatch((orgRole) -> {
                return membership.getOrganizationRoles().contains(orgRole);
            });
        }).map((cat) -> cat.toViewDTO()).toList();

        this.setOrgCategoriesUserRoles(orgId, filteredCategoriesView, userRolesIds);

        return filteredCategoriesView;
    }

    private CategoryViewDTO fetchCategory(Integer orgId, Integer categoryId) {
        var category = this.categoriesService.findOne(categoryId ,orgId);
        if(category == null) {
            throw new ResourceNotFoundException("category", categoryId);
        }

        var categoryView = category.toViewDTO();
        this.setOrgCategories(orgId, categoryView);

        return categoryView;
    }

    private void setOrgCategoriesUserRoles(Integer orgId, List<CategoryViewDTO> categories, List<Integer> rolesIds) {
        var key = this.getOrgCategoriesUserRolesCacheKey(orgId, rolesIds.toString());
        this.redisTemplate.opsForValue().set(key, categories, CACHE_TTL);
    }

    private void setOrgCategories(Integer orgId, CategoryViewDTO category) {
        var key = this.getOrgCategoriesCacheKey(orgId, category.getId());
        this.redisTemplate.opsForValue().set(key, category, CACHE_TTL);
    }

    public void invalidateOrgCategoriesUserRoles(Integer orgId, Serializable rolesIds) {
        this.redisTemplate.delete(getOrgCategoriesUserRolesCacheKey(orgId, rolesIds));
    }

    public void invalidateOrgCategories(Integer orgId, Serializable categoryId) {
        this.redisTemplate.delete(getOrgCategoriesCacheKey(orgId, categoryId));
    }

    private String getOrgCategoriesUserRolesCacheKey(Serializable orgId, Serializable userRolesIds) {
        return "org:categories:user-roles:" + orgId +":" + userRolesIds.toString();
    }

    private String getOrgCategoriesCacheKey(Serializable orgId, Serializable categoryId) {
        return "org:categories:" + orgId +":" + categoryId;
    }
}