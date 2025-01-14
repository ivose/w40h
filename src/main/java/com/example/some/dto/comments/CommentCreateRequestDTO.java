package com.example.some.dto.comments;

import lombok.Data;

@Data
public class CommentCreateRequestDTO {
    private Long postId;
    private Long parentCommentId = 0L;
    private String content;
}