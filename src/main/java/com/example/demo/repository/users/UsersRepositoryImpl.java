package com.example.demo.repository.users;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.example.demo.models.User;
import com.example.demo.repository.generic.GenericRepositoryCustom;
import com.example.demo.repository.generic.GenericRepositoryImpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.JoinType;

public class UsersRepositoryImpl extends GenericRepositoryImpl<User> implements GenericRepositoryCustom<User>, UsersRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    public UsersRepositoryImpl() {
        super(User.class);
    }

    @Override
    public Page<User> findAllWithSpecifications(Specification<User> spec, Pageable pageable) {
        return findAllWithSpecifications(spec, pageable, (query, root) -> {
            root.fetch("roles", JoinType.LEFT);
        
            query.distinct(true).select(root);
        });
    }
}
