package com.example.demo.repository.contents;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.models.Content;
import com.example.demo.repository.generic.GenericRepositoryCustom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface ContentsRepository extends JpaRepository<Content, Integer>, JpaSpecificationExecutor<Content>, ContentsRepositoryCustom, GenericRepositoryCustom<Content> {
    @Query("SELECT c FROM Content c LEFT JOIN c.user u WHERE u.id = :userId")
    Page<Content> findContentsByUserId(@Param("userId") Long userId, Pageable pageable);

    // this solves N + 1 issue
    @Query("SELECT c FROM Content c LEFT JOIN FETCH c.user")
    List<Content> findAllContents();
    
    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT c FROM Content c LEFT JOIN FETCH c.user")
    Page<Content> findAllWithFilters(Specification<Content> spec,Pageable page);

    // // also this solves N + 1 issue
    // @EntityGraph(attributePaths = {"user"}) // if u want to fetch list of contents (from users) use {"contents"}
    // List<Content> findAll();
}
