package com.example.some.dto.posts;

import com.example.some.dto.comments.CommentDetailResponseDTO;
import com.example.some.dto.comments.CommentResponseDTO;
import com.example.some.dto.reactions.ReactionSummaryDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class PostDetailsResponseDTO extends PostResponseDTO {
    private LocalDateTime updatedAt;
    private List<CommentDetailResponseDTO> recentComments;
    private List<ReactionSummaryDTO> reactions;
}