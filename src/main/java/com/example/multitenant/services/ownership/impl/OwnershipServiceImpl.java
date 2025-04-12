package com.example.multitenant.services.ownership.impl;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.example.multitenant.exceptions.ResourceNotFoundException;
import com.example.multitenant.exceptions.UnauthorizedUserException;
import com.example.multitenant.models.Organization;
import com.example.multitenant.repository.UsersRepository;
import com.example.multitenant.services.cache.RedisService;
import com.example.multitenant.services.ownership.contract.OwnershipEntity;
import com.example.multitenant.services.ownership.contract.OwnershipService;
import com.example.multitenant.utils.HelperFuncs;

/**
 * This Class meant to add a re-usable implementation to any resource that must be created/modified by user/organization
 * 
 * The behavior it will search by id + userId when user attempt to change its own resources.
 * if not found then not found error is returned, if found then updated.
 * 
 * any model that contain a userId and will be used with it must implement 'OwnershipHandlerEntity' which enable us to achieve this behavior.
 * 
 */
@Component
public abstract class OwnershipServiceImpl<TModel extends OwnershipEntity<TModel, TPrimaryKey>, TPrimaryKey extends Serializable, TRepository extends JpaRepository<TModel, TPrimaryKey>> implements OwnershipService<TModel, TPrimaryKey> {
    
    private final TRepository repository;
    private final Supplier<TModel> modelSupplier;
    private final RedisService redisService;

    @Autowired
    private UsersRepository usersRepository;

    public OwnershipServiceImpl(TRepository repository, RedisService redisService, Supplier<TModel> modelSupplier) {
        this.repository = repository;
        this.redisService = redisService;
        this.modelSupplier = modelSupplier;
    }

    public void deleteOwn(TPrimaryKey id, Integer tenantId) {
        var userId = userIdGetter();
        var result = this.repository.findById(id);
        if(!result.isPresent()) {
            throw new ResourceNotFoundException(modelSupplier.get().getClass().getName(), id);
        }

        var resource = result.get();
        var resourceUserId = resource.getUserId();
        if(userId != resourceUserId) {
            throw new UnauthorizedUserException(modelSupplier.get().getClass().getName(), id);
        }

        this.repository.delete(resource);
    }

    public TModel createOwn(TModel model, Integer tenantId){
        var userId = userIdGetter();
        var user = this.usersRepository.findById(userId).orElse(null);
        
        if(user == null){
            throw new ResourceNotFoundException(model.getClass().getName(), userId);
        }

        model.setUser(user);
        var org = new Organization();
        org.setId(tenantId);
        model.setOrganization(org);

        return this.repository.save(model);
    }

    public TModel updateOwn(TPrimaryKey id, TModel model, Integer tenantId){
        var userId = userIdGetter();

        var probe = this.modelSupplier.get();
        var org = new Organization();
        org.setId(tenantId);
        probe.setOrganization(org);

        var resource = this.repository.findOne(Example.of(probe)).orElse(null);
        if(resource == null){
            throw new ResourceNotFoundException(modelSupplier.get().getClass().getName(), id);
        }
        
        if(resource.getUserId() != userId) {
            throw new UnauthorizedUserException(modelSupplier.get().getClass().getName(), id);
        }
       
        HelperFuncs.copyNonNullProperties(model, resource);

        return this.repository.save(resource);
    }

    private Long userIdGetter() {
        var principal = this.redisService.getUserPrincipal();
        if(principal == null) {
            return null;
        }

        var userId = principal.getUser().getId();

        return userId;
    }
}
