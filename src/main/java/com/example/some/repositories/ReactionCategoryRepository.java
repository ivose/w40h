package com.example.some.repositories;

import com.example.some.entities.ReactionCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReactionCategoryRepository extends JpaRepository<ReactionCategory, Long> {
    Optional<ReactionCategory> findByName(String name);
    boolean existsByName(String name);

    @Query("SELECT rc FROM ReactionCategory rc WHERE LOWER(rc.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<ReactionCategory> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

}