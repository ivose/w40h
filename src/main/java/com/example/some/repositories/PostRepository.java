package com.example.some.repositories;

import com.example.some.entities.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.user.id IN " +
            "(SELECT f.followee.id FROM Follow f WHERE f.follower.id = :userId)")
    Page<Post> findFollowedUsersPosts(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT p FROM Post p LEFT JOIN p.reactions r " +
            "GROUP BY p ORDER BY COUNT(r) DESC")
    Page<Post> findMostReactedPosts(Pageable pageable);

    //@Query("SELECT p FROM Post p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
    //        "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    //Page<Post> searchPosts(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE " +
            "LOWER(p.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.content) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.user.username) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Post> searchPosts(@Param("searchTerm") String searchTerm, Pageable pageable);
}