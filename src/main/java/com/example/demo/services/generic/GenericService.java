package com.example.demo.services.generic;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.example.demo.exceptions.ResourceNotFoundException;

@Service
public class GenericService<TModel, ID extends Serializable> {
  
    private final JpaRepository<TModel, ID> genericRepository;

    public GenericService(JpaRepository<TModel, ID> genericRepository) {
        this.genericRepository = genericRepository;
    }

    public TModel findById(ID id) {
        var optional = this.genericRepository.findById(id);
        if(!optional.isPresent()) {
            return null;
        }

        return optional.get();
    }

    public TModel update(ID id, TModel model) {
        if(!existsById(id)) {
            throw new ResourceNotFoundException(model.getClass().getName(), id);
        }

        return this.genericRepository.save(model);
    }

    public TModel create(TModel model) {
        if(model == null) {
            throw new RuntimeException("model was received as null");
        }
        
        return this.genericRepository.save(model);
    }

    public boolean existsById(ID id) {
        return this.genericRepository.existsById(id);
    }

    public void deleteById(ID id) {
       this.genericRepository.deleteById(id);
    }

    public boolean findThenDeleteById(ID id) {
       var exists = this.existsById(id);
       if(!exists) {
        return false;
       }

       this.deleteById(id);
       return true;
    }
}
