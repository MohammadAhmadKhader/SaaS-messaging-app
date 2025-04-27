package com.example.multitenant.services.cache;

import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.stereotype.Service;

import com.example.multitenant.common.annotations.contract.LogMethod;
import com.example.multitenant.dtos.organizationroles.OrganizationRoleCacheDTO;
import com.example.multitenant.exceptions.ResourceNotFoundException;
import com.example.multitenant.models.OrganizationPermission;
import com.example.multitenant.services.categories.CategoriesService;
import com.example.multitenant.services.membership.MemberShipService;
import com.example.multitenant.services.security.OrganizationRolesService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * methods starting with 'fetch' meant to fetch directly from database and only for internal use inside this service
 * and must not be used from outside, methods starting with 'get...' are meant to be used externally to fetch the cached data.
 */
@RequiredArgsConstructor
@Service
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final OrganizationRolesService organizationRolesService;
    private final MemberShipService memberShipService;
    private final CategoriesService categoriesService;

    private static final Duration CACHE_TTL = Duration.ofMinutes(30);

    @LogMethod
    public List<String> getUserOrgPermissions(Integer orgId, long userId) {
        var cachedRolesWithPermissions = this.getOrgRolesWithPermissions(orgId);
        var userRoles = this.getUserOrgRoles(orgId, userId);

        var permsList = new ArrayList<String>();
        for (var role : userRoles) {
            var rolePerms = cachedRolesWithPermissions.get(role.getName());
            permsList.addAll(rolePerms);
        }

        return permsList;
    }
   
    @LogMethod
    @SuppressWarnings("unchecked")
    public List<OrganizationRoleCacheDTO> getUserOrgRoles(Integer orgId, long userId) {
        var key = this.getUserOrgRolesCacheKey(orgId, userId);
        var cached = this.redisTemplate.opsForValue().get(key);
        if(cached == null) {
            return this.fetchUserOrgRolesAndCache(orgId, userId);
        }

        return (List<OrganizationRoleCacheDTO>) cached;
    }   

    @LogMethod
    @SuppressWarnings("unchecked")
    public Map<String, List<String>> getOrgRolesWithPermissions(Integer orgId) {
        var cacheKey = this.getOrgRolesCacheKey(orgId);
        var cached = redisTemplate.opsForValue().get(cacheKey);

        if (cached == null) {
            return this.fetchOrgRolesWithPermissionsAndCache(orgId);
        }
        
        return (Map<String, List<String>>) cached;
    }

    @LogMethod
    @SuppressWarnings("unchecked")
    public List<Integer> getOrgCategoryWithAuthorizedRolesList(Integer orgId, Integer categoryId) {
        var cacheKey = this.getOrgCategoriesCacheKey(orgId, categoryId);
        var cached = redisTemplate.opsForValue().get(cacheKey);

        if (cached == null) {
            return this.fetchOrgCategoryAndCache(orgId, categoryId);
        }
        
        return (List<Integer>) cached;
    }

    @LogMethod
    @SuppressWarnings("unchecked")
    public Map<Integer, List<Integer>> getOrgCategoriesWithAuthorizedRolesList(Integer orgId, Integer categoryId) {
        var cacheKey = this.getOrgCategoriesCacheKey(orgId, categoryId);
        var cached = redisTemplate.opsForValue().get(cacheKey);

        if (cached == null) {
            return this.fetchOrgCategoriesAndCache(orgId);
        }
        
        return (Map<Integer, List<Integer>>) cached;
    }

    private List<OrganizationRoleCacheDTO> fetchUserOrgRolesAndCache(Integer orgId, long userId) {
        var userRoles =  this.memberShipService
        .findUserMembershipWithRoles(orgId, userId)
        .getOrganizationRoles()
        .stream()
        .map((role) -> new OrganizationRoleCacheDTO(role))
        .toList();

        this.setUserOrgRoles(orgId, userId, userRoles);

        return userRoles;
    }

    // map org role (key) -> role permissions (value)
    private Map<String, List<String>> fetchOrgRolesWithPermissionsAndCache(Integer orgId) {
        var roles = this.organizationRolesService.findAllRolesWithPermissions(orgId);
        var rolesWithPermissions = roles.stream()
        .collect(Collectors.toMap(
            role -> role.getName(),
            role -> {
                Set<OrganizationPermission> permissions = role.getOrganizationPermissions();
                return permissions.stream().map((perm) -> perm.getName()).toList();
            }
        ));

        this.setOrgRoles(orgId, rolesWithPermissions);
        return rolesWithPermissions;
    }

    // map category id (key) -> category authorized roles ids list (value)
    private Map<Integer, List<Integer>> fetchOrgCategoriesAndCache(Integer orgId) {
        var categories = this.categoriesService.findAllWithAuthorizedRoles(orgId);

        var map = new HashMap<Integer, List<Integer>>();
        categories.stream().forEach((cat) -> {
            var rolesList = cat.getAuthorizedRoles().stream().map((role) -> role.getId()).toList();
            var categoryId = cat.getId();
            map.put(categoryId, rolesList);
        
            this.setOrgCategory(orgId, categoryId, rolesList);
        });

        return map;
    }

    private List<Integer> fetchOrgCategoryAndCache(Integer orgId, Integer categoryId) {
        var category = this.categoriesService.findByIdAndOrganizationIdWithAuthorizedRoles(categoryId, orgId);
        if (category == null) {
            throw new ResourceNotFoundException("category", categoryId);
        }

        var rolesList = category.getAuthorizedRoles().stream().map((role) -> role.getId()).toList();
        this.setOrgCategory(orgId, categoryId, rolesList);

        return rolesList;
    }

    private void setOrgRoles(Integer orgId, Map<String, List<String>> rolesWithPerms) {
        var key = this.getOrgRolesCacheKey(orgId);
        this.redisTemplate.opsForValue().set(key, rolesWithPerms, CACHE_TTL);
    }
    
    private void setUserOrgRoles(Integer orgId, long userId, List<OrganizationRoleCacheDTO> roles) {
        var key = this.getUserOrgRolesCacheKey(orgId, userId);
        this.redisTemplate.opsForValue().set(key, roles, CACHE_TTL);
    }

    private void setOrgCategory(Integer orgId, Integer categoryId, List<Integer> rolesIds) {
        var key = this.getOrgCategoriesCacheKey(orgId, categoryId);
        this.redisTemplate.opsForValue().set(key, rolesIds, CACHE_TTL);
    }

    public void invalidateOrgRolesCache(Integer orgId) {
        this.redisTemplate.delete(getOrgRolesCacheKey(orgId));
    }

    public void invalidateOrgCategoriesCache(Integer orgId, Integer categoryId) {
        this.redisTemplate.delete(getOrgCategoriesCacheKey(orgId, categoryId));
    }
    
    public void invalidateUserOrgRolesCache(Integer orgId, long userId) {
        this.redisTemplate.delete(getUserOrgRolesCacheKey(orgId, userId));
    }

    public void handleRoleDeletionInvalidations(Integer orgId, Integer roleId) {
        this.invalidateOrgRolesCache(orgId);

        var usersIds = this.memberShipService.findUserIdsByOrgIdAndRoleId(orgId, roleId);
        if(!usersIds.isEmpty()) {
            var keysToInvalidate = usersIds.stream().map((userId) -> this.getUserOrgRolesCacheKey(orgId, userId)).toList();

            if(!keysToInvalidate.isEmpty()) {
                this.redisTemplate.delete(keysToInvalidate);
            }
        }
    }

    // we have used 'Serializable' incase we have to use patterns with redis such as "*" (wildcards delete/get as example)
    private String getOrgRolesCacheKey(Serializable orgId) {
        return "org:roles:" + orgId;
    }

    private String getUserOrgRolesCacheKey(Serializable orgId, Serializable userId) {
        return "user:roles:" + orgId + ":" + userId;
    }

    private String getOrgCategoriesCacheKey(Serializable orgId, Serializable categoryId) {
        return "org:categories:authorized-roles:" + orgId +":" + categoryId;
    }
}