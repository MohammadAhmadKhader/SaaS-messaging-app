package com.example.demo.services.ownership.impl;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.example.demo.services.cache.RedisService;
import com.example.demo.services.ownership.contract.OrganizationOwnershipEntity;
import com.example.demo.services.ownership.contract.OrganizationOwnershipService;
import com.example.demo.utils.HelperFuncs;

@Component
public class OrganizationOwnershipServiceImpl<TModel extends OrganizationOwnershipEntity, TPrimaryKey, TRepository extends JpaRepository<TModel, TPrimaryKey>> implements OrganizationOwnershipService<TModel, TPrimaryKey> {

    private final TRepository repository;
    private final Class<TModel> modelClass;
    private final RedisService redisService;

    public OrganizationOwnershipServiceImpl(TRepository repository, RedisService redisService, Class<TModel> modelClass) {
        this.repository = repository;
        this.redisService = redisService;
        this.modelClass = modelClass;
    }

    @Override
    public TModel updateByOrganization(TPrimaryKey id, TModel model, Integer orgId) {
        var verifiedOrgId = organizationIdGetter(orgId);

        var findResult = this.repository.findById(id);
        if(!findResult.isPresent()){
            return null;
        }

        var resource = findResult.get();
        if(resource.getOrganizationId() != verifiedOrgId) {
            return null;
        }

        HelperFuncs.copyNonNullProperties(model, resource);

        return this.repository.save(resource);
    }

    private Integer organizationIdGetter(Integer passedOrganizationId) {
        var principal = redisService.getUserPrincipal();
        if(principal == null) {
            return null;
        }

        var orgIdFromUserPrincipal = principal.getUser().getOrganizationId();
        if(orgIdFromUserPrincipal != passedOrganizationId) {
            return null;
        }

        return orgIdFromUserPrincipal;
    }

    @Override
    public void deleteByOrganization(TPrimaryKey id, TModel model, Integer orgId) {
        
        throw new UnsupportedOperationException("Unimplemented method 'deleteByOrganization'");
    }
    
}
