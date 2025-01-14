package com.example.some.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"user", "comment", "category"})
@EqualsAndHashCode(of = {"id", "user", "comment", "category"})
@Table(name = "commentreactions",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"user_id", "comment_id", "category_id"},
                name = "uq_commentreactions_user_comment_category"
        ),
        indexes = {
                @Index(name = "idx_commentreactions_user_id", columnList = "user_id"),
                @Index(name = "idx_commentreactions_comment_id", columnList = "comment_id")//,
                //@Index(name = "idx_commentreactions_category_id", columnList = "category_id")
        }
)
public class CommentReaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ReactionCategory category;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}