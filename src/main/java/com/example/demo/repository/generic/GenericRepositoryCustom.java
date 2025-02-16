package com.example.demo.repository.generic;

import java.util.function.BiConsumer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public interface GenericRepositoryCustom<TModel> {
    private Page<TModel> findAllWithSpecifications(Specification<TModel> spec, Pageable pageable, BiConsumer<CriteriaQuery<TModel>, Root<TModel>> modifier) {
        throw new UnsupportedOperationException("This method is internal and should not be accessed directly.");
    };
    public Page<TModel> findAllWithSpecifications(Specification<TModel> spec, Pageable pageable);
}