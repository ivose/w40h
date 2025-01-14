package com.example.some.repositories;

import com.example.some.entities.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByPostId(Long postId, Pageable pageable);
    List<Comment> findByUserId(Long userId);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.id = :postId")
    long countCommentsForPost(@Param("postId") Long postId);

    @Query("SELECT c FROM Comment c WHERE c.post.user.id = :userId")
    Page<Comment> findCommentsOnUserPosts(@Param("userId") Long userId, Pageable pageable);

    void deleteByPostId(Long postId);
    void deleteByUserId(Long userId);

    @Query("SELECT c FROM Comment c WHERE " +
            "LOWER(c.content) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.user.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "ORDER BY c.createdAt DESC")
    Page<Comment> searchComments(@Param("searchTerm") String searchTerm, Pageable pageable);

    Page<Comment> findByParentCommentId(Long parentCommentId, Pageable pageable);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.parentComment.id = :commentId")
    long countRepliesByCommentId(@Param("commentId") Long commentId);

    @Query("SELECT c FROM Comment c WHERE c.parentComment IS NULL AND c.post.id = :postId")
    Page<Comment> findTopLevelCommentsByPostId(@Param("postId") Long postId, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.user.id = :userId ORDER BY c.createdAt DESC")
    Page<Comment> findUserComments(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId ORDER BY c.createdAt DESC")
    Page<Comment> findAllCommentsByPostId(@Param("postId") Long postId, Pageable pageable);


}