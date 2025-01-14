package com.example.some.controllers.admin;


import com.example.some.dto.SuccessMessageDTO;
import com.example.some.dto.comments.CommentResponseDTO;
import com.example.some.dto.comments.CommentUpdateRequestDTO;
import com.example.some.models.search.SearchCriteriaModel;
import com.example.some.services.CommentService;
import com.example.some.util.constants.MessageConstants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/comments")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCommentController {
    private final CommentService commentService;

    public AdminCommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public ResponseEntity<Page<CommentResponseDTO>> getAllComments(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long userId,
            Pageable pageable) {
        SearchCriteriaModel criteria = new SearchCriteriaModel();
        criteria.setSearchTerm(search);
        criteria.setUserIds(userId != null ? List.of(userId) : null);
        return ResponseEntity.ok(commentService.searchComments(criteria, pageable));
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponseDTO> getComment(@PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.getComment(commentId));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<SuccessMessageDTO> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok(new SuccessMessageDTO(MessageConstants.COMMENT_DELETED));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponseDTO> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentUpdateRequestDTO request) {
        return ResponseEntity.ok(commentService.updateComment(commentId, request));
    }
}