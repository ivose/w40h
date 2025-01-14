package com.example.some.dto.comments;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class CommentDetailResponseDTO extends CommentResponseDTO {
    private List<CommentDetailResponseDTO> children = new ArrayList<>();
}
