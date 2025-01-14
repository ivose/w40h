package com.example.some.services;

import com.example.some.dto.SuccessMessageDTO;
import com.example.some.dto.comments.CommentDetailResponseDTO;
import com.example.some.dto.posts.PostCreateRequestDTO;
import com.example.some.dto.posts.PostResponseDTO;
import com.example.some.dto.posts.PostDetailsResponseDTO;
import com.example.some.dto.posts.PostUpdateRequestDTO;
import com.example.some.dto.reactioncategories.ReactionCategoryResponseDTO;
import com.example.some.dto.reactions.ReactionResponseDTO;
import com.example.some.dto.reactions.ReactionSummaryDTO;
import com.example.some.entities.*;
import com.example.some.models.search.SearchCriteriaModel;
import com.example.some.repositories.PostRepository;
import com.example.some.repositories.UserRepository;
import com.example.some.repositories.CommentRepository;
import com.example.some.repositories.ReactionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ReactionRepository reactionRepository;

    public PostService(PostRepository postRepository,
                       UserRepository userRepository,
                       CommentRepository commentRepository,
                       ReactionRepository reactionRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.reactionRepository = reactionRepository;
    }

    public Page<PostResponseDTO> searchPosts(SearchCriteriaModel criteria, Pageable pageable) {
        // If search criteria is provided, use search
        if (criteria != null && criteria.getSearchTerm() != null && !criteria.getSearchTerm().trim().isEmpty()) {
            return postRepository.searchPosts(criteria.getSearchTerm().trim(), pageable)
                    .map(this::convertToPostResponse);
        }

        // If user IDs are provided, filter by user
        if (criteria != null && criteria.getUserIds() != null && !criteria.getUserIds().isEmpty()) {
            // You might want to add a specific repository method for this
            return postRepository.findAll(pageable)
                    .map(this::convertToPostResponse);
        }

        // Default: return all posts
        return postRepository.findAll(pageable)
                .map(this::convertToPostResponse);
    }

    public PostResponseDTO createPost(Long userId, PostCreateRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = new Post();
        post.setUser(user);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());

        Post savedPost = postRepository.save(post);
        return convertToPostResponse(savedPost);
    }

    public PostDetailsResponseDTO getPostDetails(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        PostDetailsResponseDTO response = new PostDetailsResponseDTO();
        // Set basic post information
        response.setId(post.getId());
        response.setUserId(post.getUser().getId());
        response.setUsername(post.getUser().getUsername());
        response.setTitle(post.getTitle());
        response.setContent(post.getContent());
        response.setCreatedAt(post.getCreatedAt());
        response.setUpdatedAt(post.getUpdatedAt());

        // Add counts and recent activity
        response.setCommentsCount((int) commentRepository.countCommentsForPost(postId));
        response.setReactionsCount((int) reactionRepository.countReactionsForPost(postId));

        Pageable commentsPageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Comment> topLevelComments = commentRepository.findTopLevelCommentsByPostId(postId, commentsPageable);

        List<CommentDetailResponseDTO> commentDetails = topLevelComments.getContent().stream()
                .map(this::convertToCommentDetailResponse)
                .collect(Collectors.toList());

        response.setRecentComments(commentDetails);

        List<Reaction> reactions = reactionRepository.findByPostId(postId);
        Map<ReactionCategory, Long> reactionCounts = reactions.stream()
                .collect(Collectors.groupingBy(
                        Reaction::getCategory,
                        Collectors.counting()
                ));

        List<ReactionSummaryDTO> reactionSummaries = reactionCounts.entrySet().stream()
                .map(entry -> {
                    ReactionSummaryDTO summary = new ReactionSummaryDTO();
                    summary.setCategoryId(entry.getKey().getId());
                    summary.setCategoryName(entry.getKey().getName());
                    summary.setIcon(entry.getKey().getIcon());
                    summary.setCount(entry.getValue().intValue());
                    return summary;
                })
                .collect(Collectors.toList());

        response.setReactions(reactionSummaries);

        return response;
    }

    private ReactionResponseDTO convertToReactionResponse(Reaction reaction) {
        ReactionResponseDTO dto = new ReactionResponseDTO();
        dto.setId(reaction.getId());
        dto.setUserId(reaction.getUser().getId());
        dto.setUsername(reaction.getUser().getUsername());
        dto.setCategory(convertToCategoryResponse(reaction.getCategory()));
        dto.setCreatedAt(reaction.getCreatedAt());
        return dto;
    }

    private ReactionCategoryResponseDTO convertToCategoryResponse(ReactionCategory category) {
        return new ReactionCategoryResponseDTO(
                category.getId(),
                category.getName(),
                category.getIcon()
        );
    }


    private CommentDetailResponseDTO convertToCommentDetailResponse(Comment comment) {
        CommentDetailResponseDTO dto = new CommentDetailResponseDTO();
        dto.setId(comment.getId());
        dto.setUserId(comment.getUser().getId());
        dto.setUsername(comment.getUser().getUsername());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());

        // Convert replies recursively
        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            dto.setChildren(comment.getReplies().stream()
                    .map(this::convertToCommentDetailResponse)
                    .collect(Collectors.toList()));
        } else {
            dto.setChildren(new ArrayList<>());
        }

        return dto;
    }

    public Page<PostResponseDTO> getUserFeed(Long userId, Pageable pageable) {
        return postRepository.findFollowedUsersPosts(userId, pageable)
                .map(this::convertToPostResponse);
    }

    public PostResponseDTO convertToPostResponse(Post post) {
        PostResponseDTO dto = new PostResponseDTO();
        dto.setId(post.getId());
        dto.setUserId(post.getUser().getId());
        dto.setUsername(post.getUser().getUsername());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setCommentsCount(post.getComments().size());
        dto.setReactionsCount(post.getReactions().size());
        return dto;
    }


    //only for admin
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        postRepository.deleteById(postId);
    }

    public Page<PostResponseDTO> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable)
                .map(this::convertToPostResponse);
    }

    public PostResponseDTO updatePost(Long postId, PostUpdateRequestDTO request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        return convertToPostResponse(postRepository.save(post));
    }

    //public Page<PostResponseDTO> searchPosts(SearchCriteriaModel criteria, Pageable pageable) {
    //    return postRepository.searchPosts(criteria.getSearchTerm(), pageable)
    //            .map(this::convertToPostResponse);
    //}

    public Page<PostResponseDTO> getMyPosts(Long userId, Pageable pageable) {
        return postRepository.findByUserId(userId, pageable)
                .map(this::convertToPostResponse);
    }

    public Page<PostResponseDTO> getUserPosts(Long userId, Pageable pageable) {
        return postRepository.findByUserId(userId, pageable)
                .map(this::convertToPostResponse);
    }

    public PostResponseDTO updateMyPost(Long userId, Long postId, PostUpdateRequestDTO request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getUser().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to update this post");
        }

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        return convertToPostResponse(postRepository.save(post));
    }

    public void deleteMyPost(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getUser().getId().equals(userId)) {
            throw new RuntimeException("Not authorized to delete this post");
        }

        postRepository.delete(post);
    }
}