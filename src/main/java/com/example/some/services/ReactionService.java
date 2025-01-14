package com.example.some.services;

import com.example.some.dto.reactioncategories.ReactionCategoryResponseDTO;
import com.example.some.dto.reactions.ReactionCreateRequestDTO;
import com.example.some.dto.reactions.ReactionResponseDTO;
import com.example.some.entities.Reaction;
import com.example.some.entities.ReactionCategory;
import com.example.some.entities.Post;
import com.example.some.entities.User;
import com.example.some.repositories.ReactionRepository;
import com.example.some.repositories.ReactionCategoryRepository;
import com.example.some.repositories.PostRepository;
import com.example.some.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReactionService {
    private final ReactionRepository reactionRepository;
    private final ReactionCategoryRepository categoryRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public ReactionService(ReactionRepository reactionRepository,
                           ReactionCategoryRepository categoryRepository,
                           PostRepository postRepository,
                           UserRepository userRepository) {
        this.reactionRepository = reactionRepository;
        this.categoryRepository = categoryRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public Page<ReactionResponseDTO> getMyReactions(Long userId, Pageable pageable) {
        return reactionRepository.findByUserId(userId, pageable)
                .map(this::convertToReactionResponse);
    }

    public List<ReactionCategoryResponseDTO> getReactionCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertToCategoryResponse)
                .collect(Collectors.toList());
    }

    private ReactionCategoryResponseDTO convertToCategoryResponse(ReactionCategory category) {
        return new ReactionCategoryResponseDTO(
                category.getId(),
                category.getName(),
                category.getIcon()
        );
    }

    public ReactionResponseDTO getReaction(Long reactionId) {
        return reactionRepository.findById(reactionId)
                .map(this::convertToReactionResponse)
                .orElseThrow(() -> new RuntimeException("Reaction not found"));
    }

    public ReactionResponseDTO addReaction(Long userId, ReactionCreateRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (reactionRepository.existsByUserIdAndPostId(userId, request.getPostId())) {
            throw new RuntimeException("You have already reacted to this post");
        }

        ReactionCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Reaction category not found"));

        // Remove existing reaction if any
        reactionRepository.findByUserIdAndPostIdAndCategoryId(userId, request.getPostId(), request.getCategoryId())
                .ifPresent(reactionRepository::delete);

        Reaction reaction = new Reaction();
        reaction.setUser(user);
        reaction.setPost(post);
        reaction.setCategory(category);

        Reaction savedReaction = reactionRepository.save(reaction);
        return convertToReactionResponse(savedReaction);
    }

    public ReactionResponseDTO updateReaction(Long userId, Long reactionId, Long categoryId) {
        Reaction reaction = reactionRepository.findById(reactionId)
                .orElseThrow(() -> new RuntimeException("Reaction not found"));

        if (!reaction.getUser().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to update this reaction");
        }

        ReactionCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        reaction.setCategory(category);
        return convertToReactionResponse(reactionRepository.save(reaction));
    }

    public void deleteReaction(Long userId, Long reactionId) {
        Reaction reaction = reactionRepository.findById(reactionId)
                .orElseThrow(() -> new RuntimeException("Reaction not found"));

        if (!reaction.getUser().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to delete this reaction");
        }

        reactionRepository.delete(reaction);
    }

    private ReactionResponseDTO convertToReactionResponse(Reaction reaction) {
        ReactionResponseDTO response = new ReactionResponseDTO();
        response.setId(reaction.getId());
        response.setUserId(reaction.getUser().getId());
        response.setUsername(reaction.getUser().getUsername());
        response.setPostId(reaction.getPost().getId());
        response.setCategory(convertToReactionCategoryResponse(reaction.getCategory()));
        response.setCreatedAt(reaction.getCreatedAt());
        return response;
    }

    private com.example.some.dto.reactioncategories.ReactionCategoryResponseDTO
    convertToReactionCategoryResponse(ReactionCategory category) {
        com.example.some.dto.reactioncategories.ReactionCategoryResponseDTO response =
                new com.example.some.dto.reactioncategories.ReactionCategoryResponseDTO(0L,"","");
        response.setId(category.getId());
        response.setName(category.getName());
        response.setIcon(category.getIcon());
        return response;
    }

    public ReactionResponseDTO getMyReaction(Long userId, Long postId) {
        return reactionRepository.findByUserIdAndPostId(userId, postId)
                .map(this::convertToReactionResponse)
                .orElse(null);
    }


}