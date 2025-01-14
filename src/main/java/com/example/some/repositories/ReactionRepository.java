package com.example.some.repositories;

import com.example.some.entities.Reaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    Page<Reaction> findByUserId(Long userId, Pageable pageable);
    List<Reaction> findByUserId(Long userId);
    List<Reaction> findByPostId(Long postId);


    @Query("SELECT COUNT(r) FROM Reaction r WHERE r.post.id = :postId")
    long countReactionsForPost(@Param("postId") Long postId);

    @Query("SELECT COUNT(r) FROM Reaction r WHERE r.post.id = :postId AND r.category.id = :categoryId")
    long countReactionsByCategoryForPost(@Param("postId") Long postId, @Param("categoryId") Long categoryId);

    Optional<Reaction> findByUserIdAndPostIdAndCategoryId(Long userId, Long postId, Long categoryId);

    void deleteByUserIdAndPostId(Long userId, Long postId);

    @Query("SELECT r.post.id, COUNT(r) as reactionCount FROM Reaction r GROUP BY r.post.id ORDER BY reactionCount DESC")
    Page<Object[]> findMostReactedPosts(Pageable pageable);

    @Query("SELECT r FROM Reaction r WHERE r.post.user.id = :userId")
    List<Reaction> findReactionsOnUserPosts(@Param("userId") Long userId);

    // ReactionRepository.java
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END FROM Reaction r WHERE r.user.id = :userId AND r.post.id = :postId")
    boolean existsByUserIdAndPostId(@Param("userId") Long userId, @Param("postId") Long postId);

    @Query("SELECT r FROM Reaction r WHERE r.user.id = :userId AND r.post.id = :postId")
    Optional<Reaction> findByUserIdAndPostId(@Param("userId") Long userId, @Param("postId") Long postId);
}