package com.example.some.dto.comments;

import lombok.Data;

@Data
public class CommentReplyRequestDTO {
    private Long parentCommentId;
    private Long postId;
    private String content;
}