
package com.example.some.services;

import com.example.some.dto.commentreactions.CommentReactionCreateRequestDTO;
import com.example.some.dto.commentreactions.CommentReactionResponseDTO;
import com.example.some.dto.reactions.ReactionCreateRequestDTO;
import com.example.some.dto.reactions.ReactionResponseDTO;
import com.example.some.dto.reactioncategories.ReactionCategoryResponseDTO;
import com.example.some.entities.*;
import com.example.some.repositories.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CommentReactionService {
    private final CommentReactionRepository crr;
    private final ReactionCategoryRepository categoryRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public CommentReactionService(CommentReactionRepository crr,
                                  ReactionCategoryRepository categoryRepository,
                                  CommentRepository commentRepository,
                                  UserRepository userRepository) {
        this.crr = crr;
        this.categoryRepository = categoryRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    public Page<CommentReactionResponseDTO> getMyReactions(Long userId, Pageable pageable) {
        return crr.findByUserId(userId, pageable)
                .map(this::convertToReactionResponse);
    }

    public CommentReactionResponseDTO getReaction(Long reactionId) {
        return crr.findById(reactionId)
                .map(this::convertToReactionResponse)
                .orElseThrow(() -> new RuntimeException("Reaction not found"));
    }

    public CommentReactionResponseDTO addReaction(Long userId, CommentReactionCreateRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = commentRepository.findById(request.getCommentId())
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (crr.existsByUserIdAndCommentId(userId, request.getCommentId())) {
            throw new RuntimeException("You have already reacted to this comment");
        }

        ReactionCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Reaction category not found"));

        // Remove existing reaction if any
        crr.findByUserIdAndCommentIdAndCategoryId(userId, request.getCommentId(), request.getCategoryId())
                .ifPresent(crr::delete);

        CommentReaction reaction = new CommentReaction();
        reaction.setUser(user);
        reaction.setComment(comment);
        reaction.setCategory(category);

        CommentReaction savedReaction = crr.save(reaction);
        return convertToReactionResponse(savedReaction);
    }

    public CommentReactionResponseDTO updateReaction(Long userId, Long reactionId, Long categoryId) {
        CommentReaction reaction = crr.findById(reactionId)
                .orElseThrow(() -> new RuntimeException("Reaction not found"));

        if (!reaction.getUser().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to update this reaction");
        }

        ReactionCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        reaction.setCategory(category);
        return convertToReactionResponse(crr.save(reaction));
    }

    public void deleteReaction(Long userId, Long reactionId) {
        CommentReaction reaction = crr.findById(reactionId)
                .orElseThrow(() -> new RuntimeException("Reaction not found"));

        if (!reaction.getUser().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to delete this reaction");
        }

        crr.delete(reaction);
    }

    private CommentReactionResponseDTO convertToReactionResponse(CommentReaction cr) {
        CommentReactionResponseDTO response = new CommentReactionResponseDTO();
        response.setId(cr.getId());
        response.setUserId(cr.getUser().getId());
        response.setUsername(cr.getUser().getUsername());
        response.setCommentId(cr.getComment().getId());
        response.setCategory(convertToReactionCategoryResponse(cr.getCategory()));
        response.setCreatedAt(cr.getCreatedAt());
        return response;
    }

    private ReactionCategoryResponseDTO convertToReactionCategoryResponse(ReactionCategory category) {
        return new ReactionCategoryResponseDTO(
                category.getId(),
                category.getName(),
                category.getIcon()
        );
    }


}