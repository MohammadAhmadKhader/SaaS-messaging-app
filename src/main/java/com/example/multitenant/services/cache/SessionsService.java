package com.example.multitenant.services.cache;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.multitenant.models.OrganizationPermission;
import com.example.multitenant.services.membership.MemberShipService;
import com.example.multitenant.services.security.OrganizationRolesService;


@Service
public class SessionsService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final OrganizationRolesService organizationRolesService;
    private final MemberShipService memberShipService;

    private static final Duration CACHE_TTL = Duration.ofMinutes(30);

    public SessionsService(RedisTemplate<String, Object> redisTemplate, OrganizationRolesService organizationRolesService, MemberShipService memberShipService) {
        this.redisTemplate = redisTemplate;
        this.organizationRolesService = organizationRolesService;
        this.memberShipService = memberShipService;
    }

    public List<String> getUserOrgPermissions(Integer orgId, long userId) {
        var cachedRolesWithPermissions = this.getOrgRolesWithPermissions(orgId);
        var userRoles = this.getUserOrgRoles(orgId, userId);

        var permsList = new ArrayList<String>();
        for (var role : userRoles) {
            var rolePerms = cachedRolesWithPermissions.get(role);
            permsList.addAll(rolePerms);
        }

        return permsList;
    }
    
    public List<String> getUserOrgRoles(Integer orgId, long userId) {
        var key = this.getUserOrgRolesCacheKey(orgId, userId);
        var cached = this.redisTemplate.opsForValue().get(key);
        if(cached == null) {
            return this.fetchUserOrgRolesAndCache(orgId, userId);
        }

        return (List<String>) cached;
    } 
    

    public Map<String, List<String>> getOrgRolesWithPermissions(Integer orgId) {
        var cacheKey = this.getOrgRolesCacheKey(orgId);
        var cached = redisTemplate.opsForValue().get(cacheKey);

        if (cached == null) {
            return this.fetchOrgRolesWithPermissionsAndCache(orgId);
        }
        
        return (Map<String, List<String>>) cached;
    }

    private List<String> fetchUserOrgRolesAndCache(Integer orgId, long userId) {
        var userRoles =  this.memberShipService
        .findUserMembershipWithRoles(orgId, userId)
        .getOrganizationRoles()
        .stream()
        .map((role) -> role.getName())
        .toList();

        this.setUserOrgRoles(orgId, userId, userRoles);

        return userRoles;
    }

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

    private void setOrgRoles(Integer orgId, Map<String, List<String>> rolesWithPerms) {
        var key = this.getOrgRolesCacheKey(orgId);
        this.redisTemplate.opsForValue().set(key, rolesWithPerms, CACHE_TTL);
    }
    
    private void setUserOrgRoles(Integer orgId, long userId, List<String> roles) {
        var key = this.getUserOrgRolesCacheKey(orgId, userId);
        this.redisTemplate.opsForValue().set(key, roles, CACHE_TTL);
    }

    public void invalidateOrgRolesCache(Integer orgId) {
        this.redisTemplate.delete(getOrgRolesCacheKey(orgId));
    }
    
    public void invalidateUserOrgRolesCache(Integer orgId, long userId) {
        this.redisTemplate.delete(getUserOrgRolesCacheKey(orgId, userId));
    }

    private String getOrgRolesCacheKey(Integer orgId) {
        return "org:roles:" + orgId;
    }

    private String getUserOrgRolesCacheKey(Integer orgId, long userId) {
        return "user:roles:" + orgId + ":" + userId;
    }
}