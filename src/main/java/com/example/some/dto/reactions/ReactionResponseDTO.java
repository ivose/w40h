package com.example.some.dto.reactions;

import com.example.some.dto.reactioncategories.ReactionCategoryResponseDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReactionResponseDTO {
    private Long id;
    private Long userId;
    private String username;
    private Long postId;
    private ReactionCategoryResponseDTO category;
    private LocalDateTime createdAt;
}