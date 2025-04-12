package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.models.User;

@Repository
public interface UsersRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);

    // // this solves the issue of N + 1 query
    // @EntityGraph(attributePaths = {"contents"})
    // List<User> findAll();

    @EntityGraph(attributePaths = {"contents"}) // Eagerly load contents
    @Query("SELECT u FROM User u") // Explicit query to select all users
    List<User> findAllWithContents();
}
