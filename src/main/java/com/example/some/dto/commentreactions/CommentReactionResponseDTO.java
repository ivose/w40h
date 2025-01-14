package com.example.some.dto.commentreactions;

import com.example.some.dto.reactioncategories.ReactionCategoryResponseDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentReactionResponseDTO {
    private Long id;
    private Long userId;
    private String username;
    private Long commentId;
    private ReactionCategoryResponseDTO category;
    private LocalDateTime createdAt;
}