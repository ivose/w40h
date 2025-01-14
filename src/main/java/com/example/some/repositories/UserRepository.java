package com.example.some.repositories;

import com.example.some.dto.users.UserSearchDTO;
import com.example.some.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.nio.channels.FileChannel;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByResetToken(String resetToken);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    @Query("SELECT c FROM User c WHERE " +
            "LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<User> searchByNameAndEmail(String trim, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.id != :currentUserId AND " +
            "(LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<User> findByIdNotAndSearchCriteria(Long currentUserId, String searchTerm, Pageable pageable);

    Page<User> findByIdNot(Long currentUserId, Pageable pageable);

    //FileChannel searchUsers(String searchTerm, Pageable pageable);
    @Query("SELECT NEW com.example.some.dto.users.UserSearchDTO(u.id, u.username, u.fullName, u.born, u.isActive, u.createdAt) " +
            "FROM User u WHERE " +
            "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<UserSearchDTO> searchUsers(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT u FROM User u WHERE " +
            "(LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND u.id != :excludedUserId")
    Page<User> searchUsersExcluding(@Param("searchTerm") String searchTerm,
                                    @Param("excludedUserId") Long excludedUserId,
                                    Pageable pageable);


}