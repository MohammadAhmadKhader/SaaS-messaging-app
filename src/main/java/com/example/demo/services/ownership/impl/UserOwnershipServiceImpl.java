package com.example.demo.services.ownership.impl;
import java.beans.FeatureDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.AccessDeniedException;
import java.util.Arrays;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;

import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.exceptions.UnauthorizedUserException;
import com.example.demo.repository.users.UsersRepository;
import com.example.demo.services.cache.RedisService;
import com.example.demo.services.ownership.contract.UserOwnershipEntity;
import com.example.demo.services.ownership.contract.UserOwnershipService;
import com.example.demo.utils.HelperFuncs;

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
public class UserOwnershipServiceImpl<TModel extends UserOwnershipEntity<TModel, TPrimaryKey>, TPrimaryKey extends Serializable, TRepository extends JpaRepository<TModel, TPrimaryKey>> implements UserOwnershipService<TModel, TPrimaryKey> {
    
    private final TRepository repository;
    private final Class<TModel> modelClass;
    private final RedisService redisService;

    @Autowired
    private UsersRepository usersRepository;

    public UserOwnershipServiceImpl(TRepository repository, RedisService redisService, Class<TModel> modelClass) {
        this.repository = repository;
        this.redisService = redisService;
        this.modelClass = modelClass;
    }

    public void deleteOwn(TPrimaryKey id) {
        var userId = userIdGetter();
        var result = this.repository.findById(id);
        if(!result.isPresent()) {
            throw new ResourceNotFoundException(modelClass.getName(), id);
        }

        var resource = result.get();
        var resourceUserId = resource.getUserId();
        if(userId != resourceUserId) {
            throw new UnauthorizedUserException(modelClass.getName(), id);
        }

        this.repository.delete(resource);
    }

    public TModel createOwn(TModel model){
        var userId = userIdGetter();
        var findUserResult = usersRepository.findById(userId);
        
        if(!findUserResult.isPresent()){
            throw new ResourceNotFoundException(findUserResult.getClass().getName(), userId);
        }

        var user = findUserResult.get();
        model.setUser(user);

        return this.repository.save(model);
    }

    public TModel updateOwn(TPrimaryKey id, TModel model){
        var userId = userIdGetter();

        var result = this.repository.findById(id);
        if(!result.isPresent()){
            throw new ResourceNotFoundException(modelClass.getName(), id);
        }
        
        var resource = result.get();
        if(resource.getUserId() != userId) {
            throw new UnauthorizedUserException(modelClass.getName(), id);
        }
       
        HelperFuncs.copyNonNullProperties(model, resource);

        return this.repository.save(resource);
    }

    private Long userIdGetter() {
        var principal = redisService.getUserPrincipal();
        if(principal == null) {
            return null;
        }

        var userId = principal.getUser().getId();

        return userId;
    }
}
