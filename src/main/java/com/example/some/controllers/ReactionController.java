package com.example.some.controllers;

import com.example.some.dto.SuccessMessageDTO;
import com.example.some.dto.reactioncategories.ReactionCategoryResponseDTO;
import com.example.some.dto.reactions.*;
import com.example.some.services.ReactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.*;

@RestController
@RequestMapping("/api/reactions")
public class ReactionController {
    private final ReactionService reactionService;

    public ReactionController(ReactionService reactionService) {
        this.reactionService = reactionService;
    }

    @GetMapping
    public ResponseEntity<Page<ReactionResponseDTO>> getMyReactions(
            @RequestAttribute("userId") Long userId,
            Pageable pageable) {
        return ResponseEntity.ok(reactionService.getMyReactions(userId, pageable));
    }

    @GetMapping("/categories")
    public ResponseEntity<java.util.List<ReactionCategoryResponseDTO>> getReactionCategories() {
        return ResponseEntity.ok(reactionService.getReactionCategories());
    }

    @GetMapping("/mine/{postId}")
    public ResponseEntity<ReactionResponseDTO> getMyReaction(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long postId) {
        return ResponseEntity.ok(reactionService.getMyReaction(userId, postId));
    }

    @PostMapping
    public ResponseEntity<ReactionResponseDTO> addReaction(
            @RequestAttribute("userId") Long userId,
            @RequestBody ReactionCreateRequestDTO request) {
        return ResponseEntity.ok(reactionService.addReaction(userId, request));
    }

    @GetMapping("/{reactionId}")
    public ResponseEntity<ReactionResponseDTO> getReaction(
            @PathVariable Long reactionId) {
        return ResponseEntity.ok(reactionService.getReaction(reactionId));
    }


    @PutMapping("/{reactionId}/{categoryId}")
    public ResponseEntity<ReactionResponseDTO> updateReaction(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long reactionId,
            @PathVariable Long categoryId) {
        return ResponseEntity.ok(reactionService.updateReaction(userId, reactionId, categoryId));
    }

    @DeleteMapping("/{reactionId}")
    public ResponseEntity<SuccessMessageDTO> deleteReaction(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long reactionId) {
        reactionService.deleteReaction(userId, reactionId);
        return ResponseEntity.ok(new SuccessMessageDTO("Reaction deleted successfully"));
    }
}