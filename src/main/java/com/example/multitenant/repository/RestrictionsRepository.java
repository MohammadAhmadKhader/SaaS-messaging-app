package com.example.multitenant.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.multitenant.models.Restriction;

@Repository
public interface RestrictionsRepository extends GenericRepository<Restriction, Integer> {
    @Query(value = """
        SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END
        FROM restrictions
        WHERE user_id = :userId AND until > CURRENT_TIMESTAMP
    """, nativeQuery = true)
    boolean isUserRestricted(@Param("userId") Long userId);
}
