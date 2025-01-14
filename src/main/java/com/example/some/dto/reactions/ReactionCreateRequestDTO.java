package com.example.some.dto.reactions;

import lombok.Data;

@Data
public class ReactionCreateRequestDTO {
    private Long postId;
    private Long categoryId;
}