package com.example.demo.repository.generic;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import java.util.function.BiConsumer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;

import com.example.demo.models.Content;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.EntityType;

public class GenericRepositoryImpl<TModel> {
    @PersistenceContext
    private EntityManager entityManager;
    
    private final Class<TModel> modelClass;

    public GenericRepositoryImpl(Class<TModel> modelClass) {
        this.modelClass = modelClass;
    }

    protected Page<TModel> findAllWithSpecifications(Specification<TModel> spec, Pageable pageable, BiConsumer<CriteriaQuery<TModel>, Root<TModel>> modifier){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<TModel> query  = cb.createQuery(modelClass);
        Root<TModel> root = query.from(modelClass);

        if(modifier != null) {
            modifier.accept(query, root);
        }

        if(spec != null) {
            var predicate = spec.toPredicate(root, query, cb);

            if(predicate != null) {
                query.where(predicate);
            }
        }

        TypedQuery<TModel> typedQuery = entityManager.createQuery(query)
        .setFirstResult((int) pageable.getOffset())
        .setMaxResults(pageable.getPageSize());

        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var count = scope.fork(()-> countTotalWithSpec(spec));
            var list = scope.fork(()-> typedQuery.getResultList());
        
            scope.join();
            scope.throwIfFailed();
            
            return new PageImpl<TModel>(list.get(), pageable, count.get());
        }  catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error occurred during task execution", e);
        }
    }

    private Long countTotalWithSpec(Specification<TModel> spec){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(long.class);

        EntityType<TModel> entity = entityManager.getMetamodel().entity(modelClass);
        Root<TModel> root = countQuery.from(entity);


        countQuery.select(cb.countDistinct(root));
        if(spec != null) {
            var predicate = spec.toPredicate(root, countQuery, cb);
            
            if(predicate != null) {
                countQuery.where(predicate);
            }
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }
}
