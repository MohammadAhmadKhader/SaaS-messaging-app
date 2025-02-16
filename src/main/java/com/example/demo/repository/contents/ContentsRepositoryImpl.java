package com.example.demo.repository.contents;


import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import com.example.demo.models.Content;
import com.example.demo.repository.generic.GenericRepositoryCustom;
import com.example.demo.repository.generic.GenericRepositoryImpl;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.MethodNotAllowedException;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;

@Repository
public class ContentsRepositoryImpl extends GenericRepositoryImpl<Content> implements GenericRepositoryCustom<Content>, ContentsRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    public ContentsRepositoryImpl() {
        super(Content.class);
    }

    @Override
    public Page<Content> findAllWithSpecifications(Specification<Content> spec, Pageable pageable) {
        return findAllWithSpecifications(spec, pageable, (query, root) -> {
            root.fetch("user", JoinType.LEFT)
                .fetch("roles", JoinType.LEFT);
        

            query.distinct(true).select(root);
        });
    }
}
