package com.example.some.dto.commentreactions;

import lombok.Data;

@Data
public class CommentReactionCreateRequestDTO {
    private Long commentId;
    private Long categoryId;
}