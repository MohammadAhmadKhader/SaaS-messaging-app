package com.example.multitenant.services.generic;

import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.example.multitenant.exceptions.InvalidOperationException;
import com.example.multitenant.exceptions.ResourceNotFoundException;
import com.example.multitenant.repository.GenericRepository;

public abstract class GenericService<TModel, ID extends Serializable> {

    protected final GenericRepository<TModel, ID> repository;

    protected GenericService(GenericRepository<TModel, ID> repository) {
        this.repository = repository;
    }

    public TModel findById(ID id) {
        return this.repository.findById(id).orElse(null);
    }

    // TODO: this is invalid, will be removed
    public TModel update(ID id, TModel model) {
        if (!existsById(id)) {
            throw new ResourceNotFoundException(model.getClass().getName(), id);
        }
        
        return this.repository.save(model);
    }

    // TODO: will be refactored to a clearer approach
    public TModel create(TModel model) {
        if (model == null) {
            throw new InvalidOperationException("model was received as null");
        }

        return this.repository.save(model);
    }

    public TModel save(TModel model) {
        return this.repository.save(model);
    }

    public List<TModel> createMany(List<TModel> model) {
        if (model.isEmpty()) {
            throw new InvalidOperationException("list is empty");
        }
        return this.repository.saveAll(model);
    }

    public List<TModel> createManyAndFlush(List<TModel> model) {
        if (model.isEmpty()) {
            throw new InvalidOperationException("list is empty");
        }
        return this.repository.saveAllAndFlush(model);
    }

    public TModel findThenUpdate(ID id, Consumer<TModel> patcher) {
        var model = findById(id);
        if (model == null) {
            return null;
        }

        patcher.accept(model);
        save(model);

        return model;
    }

    public boolean existsById(ID id) {
        return this.repository.existsById(id);
    }

    public void deleteById(ID id) {
        this.repository.deleteById(id);
    }

    public boolean findThenDeleteById(ID id) {
        var exists = this.existsById(id);
        if (!exists) {
            return false;
        }
        this.deleteById(id);
        return true;
    }
}