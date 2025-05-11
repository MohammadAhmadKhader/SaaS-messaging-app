package com.example.multitenant.services.helperservices;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.example.multitenant.dtos.shared.CursorPage;
import com.example.multitenant.exceptions.AsyncOperationException;
import com.example.multitenant.exceptions.UnknownException;
import com.example.multitenant.utils.VirtualThreadsUtils;

import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.EntityType;

@Component
public abstract class SpecificationsService<TModel, PrimaryKey extends Serializable> {
    @PersistenceContext
    private EntityManager entityManager; 
    private final Class<TModel> modelClass;

    public SpecificationsService(Class<TModel> modelClass) {
        this.modelClass = modelClass;
    }

    public Page<TModel> findAllWithSpecifications(Pageable pageable, Specification<TModel> spec, BiConsumer<CriteriaQuery<TModel>, Root<TModel>> modifier){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<TModel> query = cb.createQuery(modelClass);
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

        var orders = new ArrayList<Order>();
        for (Sort.Order order : pageable.getSort()) {
            Path<Object> path = root.get(order.getProperty());
            orders.add(order.isAscending() ? cb.asc(path) : cb.desc(path));
        }
        
        if (!orders.isEmpty()) {
            query.orderBy(orders);
        }

        TypedQuery<TModel> typedQuery = entityManager.createQuery(query)
        .setFirstResult((int) pageable.getOffset())
        .setMaxResults(pageable.getPageSize());

        var tasksResults = VirtualThreadsUtils.run(
            ()-> typedQuery.getResultList(),
            () -> countTotalWithSpec(spec)
        );
        
        var list = tasksResults.getLeft();
        var count = tasksResults.getRight();
            
        return new PageImpl<TModel>(list, pageable, count);
    }

    public CursorPage<TModel, PrimaryKey> findAllWithCursor(Specification<TModel> spec, Long cursor, int size, String idFieldName) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<TModel> critQuery = cb.createQuery(this.modelClass);
        Root<TModel> root = critQuery.from(this.modelClass);

        var predicate = spec.toPredicate(root, critQuery, cb);

        var predicates = new ArrayList<Predicate>();
        if (predicate != null) {
            predicates.add(predicate);
        }

        if (cursor != null) {
            predicates.add(cb.lessThan(root.get(idFieldName), cursor));
        }

        if (!predicates.isEmpty()) {
            critQuery.where(cb.and(predicates.toArray(new Predicate[0])));
        } else {
            critQuery.where(cb.conjunction());
        }

        var arrOfPredicates = predicates.toArray(new Predicate[0]);
        critQuery
          .where(arrOfPredicates)
          .orderBy(cb.desc(root.get(idFieldName)));

        var query = entityManager.createQuery(critQuery);
        query.setMaxResults(size + 1);

        var results = query.getResultList();
        boolean hasNext = results.size() > size;
        if (hasNext) {
            results = results.subList(0, size);
        }
    
        PrimaryKey nextCursor = null;
        if (!results.isEmpty()) {
            try {
                var lastItem = results.get(results.size() - 1);
                var field = modelClass.getDeclaredField(idFieldName);
                field.setAccessible(true);
                nextCursor = (PrimaryKey) field.get(lastItem);

            } catch (NoSuchFieldException | IllegalAccessException ex) {
                throw new UnknownException("failed to extract cursor from result object", ex);
            }
        }

        return CursorPage.of(results, nextCursor, hasNext);
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