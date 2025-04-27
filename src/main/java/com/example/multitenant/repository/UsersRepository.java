package com.example.multitenant.repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import com.example.multitenant.models.User;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface UsersRepository extends GenericRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);

    @EntityGraph(attributePaths = {"contents"})
    @Query("SELECT u FROM User u")
    List<User> findAllWithContents();

    @Query("SELECT u FROM User u JOIN FETCH u.roles r JOIN FETCH r.permissions p WHERE u.email = :email")
    User findByEmailWithPermissions(@Param("email") String email);
}
