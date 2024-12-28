package com.example.some.repository;

import com.example.some.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
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
}
