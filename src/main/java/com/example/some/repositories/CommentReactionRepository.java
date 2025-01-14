package com.example.some.repositories;

import com.example.some.entities.CommentReaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentReactionRepository extends JpaRepository<CommentReaction, Long> {
    Page<CommentReaction> findByUserId(Long userId, Pageable pageable);
    List<CommentReaction> findByUserId(Long userId);
    List<CommentReaction> findByCommentId(Long commentId);


    @Query("SELECT COUNT(r) FROM CommentReaction r WHERE r.comment.id = :commentId")
    long countReactionsForComment(@Param("commentId") Long commentId);

    @Query("SELECT COUNT(r) FROM CommentReaction r WHERE r.comment.id = :commentId AND r.category.id = :categoryId")
    long countReactionsByCategoryForComment(@Param("commentId") Long commentId, @Param("categoryId") Long categoryId);

    Optional<CommentReaction> findByUserIdAndCommentIdAndCategoryId(Long userId, Long commentId, Long categoryId);

    void deleteByUserIdAndCommentId(Long userId, Long commentId);

    @Query("SELECT r.comment.id, COUNT(r) as reactionCount FROM CommentReaction r GROUP BY r.comment.id ORDER BY reactionCount DESC")
    Page<Object[]> findMostReactedComments(Pageable pageable);

    @Query("SELECT r FROM CommentReaction r WHERE r.comment.user.id = :userId")
    List<CommentReaction> findReactionsOnUserComments(@Param("userId") Long userId);

    // ReactionRepository.java
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END FROM CommentReaction r WHERE r.user.id = :userId AND r.comment.id = :commentId")
    boolean existsByUserIdAndCommentId(@Param("userId") Long userId, @Param("commentId") Long commentId);
}