package com.example.some.dto.comments;


import lombok.Data;

@Data
public class CommentReplyResponseDTO extends CommentResponseDTO {
    private Long parentCommentId;
    private int repliesCount;
}
