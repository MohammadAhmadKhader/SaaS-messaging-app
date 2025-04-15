package com.example.multitenant.repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.multitenant.models.User;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface UsersRepository extends GenericRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);

    // // this solves the issue of N + 1 query
    // @EntityGraph(attributePaths = {"contents"})
    // List<User> findAll();

    @EntityGraph(attributePaths = {"contents"}) // Eagerly load contents
    @Query("SELECT u FROM User u") // Explicit query to select all users
    List<User> findAllWithContents();

    @Query("SELECT u FROM User u JOIN FETCH u.roles r JOIN FETCH r.permissions p WHERE u.email = :email")
    User findByEmailWithPermissions(@Param("email") String email);
}
