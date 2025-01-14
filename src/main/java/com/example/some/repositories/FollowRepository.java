package com.example.some.repositories;

import com.example.some.entities.Follow;
import com.example.some.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    List<Follow> findByFollowerId(Long followerId);
    List<Follow> findByFolloweeId(Long followeeId);

    @Query("SELECT COUNT(f) FROM Follow f WHERE f.followee.id = :userId")
    long countFollowers(@Param("userId") Long userId);

    @Query("SELECT COUNT(f) FROM Follow f WHERE f.follower.id = :userId")
    long countFollowing(@Param("userId") Long userId);

    void deleteByFollowerIdAndFolloweeId(Long followerId, Long followeeId);

    @Query("SELECT f.followee FROM Follow f WHERE f.follower.id = :userId")
    Page<User> findFolloweesByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT f.follower FROM Follow f WHERE f.followee.id = :userId")
    Page<User> findFollowersByUserId(@Param("userId") Long userId, Pageable pageable);

    boolean existsByFollowerIdAndFolloweeId(Long followerId, Long followeeId);
}