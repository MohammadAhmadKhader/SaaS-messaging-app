package com.example.demo.services.generic;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.example.demo.dtos.shared.IViewDTO;

@Service
public class GenericService<TModel extends IViewDTO<ViewModel>, ID extends Serializable, ViewModel> {
  
    private final JpaRepository<TModel, ID> genericRepository;

    public GenericService(JpaRepository<TModel, ID> genericRepository) {
        this.genericRepository = genericRepository;
    }

    public TModel findById(ID id) {
        var optional = genericRepository.findById(id);
        if(!optional.isPresent()) {
            return null;
        }

        return optional.get();
    }

    public ViewModel findByIdAsView(ID id) {
        var optional = genericRepository.findById(id);
        if(!optional.isPresent()) {
            return null;
        }

        return optional.get().toViewDTO();
    }

    public TModel update(ID id, TModel model) {
        if(!existsById(id)) {
            throw new RuntimeException(String.format("%s with id: '%s' does not exist", model.getClass().getName(), id));
        }

        return genericRepository.save(model);
    }

    public TModel create(TModel model) {
        return genericRepository.save(model);
    }

    public ViewModel createAndReturnAsView(TModel model) {
        return create(model).toViewDTO();
    }

    public ViewModel updateAndReturnAsView(ID id, TModel model) {
        return update(id, model).toViewDTO();
    }

    public boolean existsById(ID id) {
        return genericRepository.existsById(id);
    }

    public void deleteById(ID id) {
        genericRepository.deleteById(id);
    }
}
