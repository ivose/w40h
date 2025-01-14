package com.example.some.controllers;

import com.example.some.dto.SuccessMessageDTO;
import com.example.some.dto.comments.*;
import com.example.some.services.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public ResponseEntity<Page<CommentResponseDTO>> getUserComments(
            @RequestAttribute("userId") Long userId,
            Pageable pageable) {
        return ResponseEntity.ok(commentService.getUserComments(userId, pageable));
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<Page<CommentResponseDTO>> getUserCommentsFromPost(
            @PathVariable Long postId,
            Pageable pageable) {
        return ResponseEntity.ok(commentService.findAllCommentsByPostId(postId, pageable));
    }

    @PostMapping
    public ResponseEntity<CommentResponseDTO> createComment(
            @RequestAttribute("userId") Long userId,
            @RequestBody CommentCreateRequestDTO request) {
        return ResponseEntity.ok(commentService.createComment(userId, request));
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDetailResponseDTO> getCommentWithReplies(
            @PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.getCommentWithReplies(commentId));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponseDTO> updateComment(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long commentId,
            @RequestBody CommentUpdateRequestDTO request) {
        return ResponseEntity.ok(commentService.updateOwnComment(userId, commentId, request));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<SuccessMessageDTO> deleteComment(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long commentId) {
        commentService.deleteOwnComment(userId, commentId);
        return ResponseEntity.ok(new SuccessMessageDTO("Comment deleted successfully"));
        //or simply return ResponseEntity.ok().build();
    }
}