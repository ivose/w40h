package com.example.some.controllers;

import com.example.some.dto.SuccessMessageDTO;
import com.example.some.dto.commentreactions.CommentReactionCreateRequestDTO;
import com.example.some.dto.commentreactions.CommentReactionResponseDTO;
import com.example.some.dto.reactions.ReactionCreateRequestDTO;
import com.example.some.dto.reactions.ReactionResponseDTO;
import com.example.some.services.CommentReactionService;
import com.example.some.services.ReactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/commentreactions")
public class CommentReactionController {
    private final CommentReactionService crs;

    public CommentReactionController(CommentReactionService crs) {
        this.crs = crs;
    }

    @GetMapping
    public ResponseEntity<Page<CommentReactionResponseDTO>> getMyReactions(
            @RequestAttribute("userId") Long userId,
            Pageable pageable) {
        return ResponseEntity.ok(crs.getMyReactions(userId, pageable));
    }

    @PostMapping
    public ResponseEntity<CommentReactionResponseDTO> addReaction(
            @RequestAttribute("userId") Long userId,
            @RequestBody CommentReactionCreateRequestDTO request) {
        return ResponseEntity.ok(crs.addReaction(userId, request));
    }

    @GetMapping("/{reactionId}")
    public ResponseEntity<CommentReactionResponseDTO> getReaction(
            @PathVariable Long reactionId) {
        return ResponseEntity.ok(crs.getReaction(reactionId));
    }


    @PutMapping("/{reactionId}/{categoryId}")
    public ResponseEntity<CommentReactionResponseDTO> updateReaction(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long reactionId,
            @PathVariable Long categoryId) {
        return ResponseEntity.ok(crs.updateReaction(userId, reactionId, categoryId));
    }

    @DeleteMapping("/{reactionId}")
    public ResponseEntity<SuccessMessageDTO> deleteReaction(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long reactionId) {
        crs.deleteReaction(userId, reactionId);
        return ResponseEntity.ok(new SuccessMessageDTO("Reaction deleted successfully"));
    }
}