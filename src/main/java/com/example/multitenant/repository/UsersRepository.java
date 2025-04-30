package com.example.multitenant.repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import com.example.multitenant.models.User;

import org.springframework.data.repository.query.Param;

@Repository
public interface UsersRepository extends GenericRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);

    @Query("SELECT u FROM User u JOIN FETCH u.roles r JOIN FETCH r.permissions p WHERE u.email = :email")
    User findByEmailWithPermissions(@Param("email") String email);

    @Query("""
        SELECT CASE WHEN COUNT(uf) > 0 THEN true ELSE false END FROM User u  
        JOIN u.friends uf WHERE u.id = :userId AND uf.id = :friendId           
    """)
    boolean isFriend(@Param("userId") Long userId, @Param("friendId") Long friendId);

    @Query("""
        SELECT u FROM User u
        LEFT JOIN u.friends f
        WHERE u.id IN :ids
    """)
    List<User> findAllByIdsWithFriends(@Param("ids") List<Long> ids);
}
