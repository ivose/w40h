package com.example.some.services;

import com.example.some.dto.comments.CommentResponseDTO;
import com.example.some.dto.posts.PostResponseDTO;
import com.example.some.dto.users.UserResponseDTO;
import com.example.some.dto.users.UserSearchDTO;
import com.example.some.models.search.SearchCriteriaModel;
import com.example.some.repositories.CommentRepository;
import com.example.some.repositories.FollowRepository;
import com.example.some.repositories.PostRepository;
import com.example.some.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SearchService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostService postService;
    private final CommentRepository commentRepository;
    private final FollowRepository followRepository;

    public SearchService(PostRepository postRepository,
                         UserRepository userRepository,
                         PostService postService,
                         CommentRepository commentRepository,
                         FollowRepository followRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postService = postService;
        this.commentRepository = commentRepository;
        this.followRepository = followRepository;
    }

    public Page<PostResponseDTO> searchPosts(SearchCriteriaModel criteria, Pageable pageable) {
        return postRepository.searchPosts(criteria.getSearchTerm(), pageable)
                .map(postService::convertToPostResponse);
    }

    public Page<UserSearchDTO> searchUsers(SearchCriteriaModel criteria, Pageable pageable) {
        Page<UserSearchDTO> users = userRepository.searchUsers(criteria.getSearchTerm(), pageable);

        // Populate follower counts if needed
        users.forEach(user -> {
            user.setFollowersCount((int) followRepository.countFollowers(user.getId()));
            user.setFollowingCount((int) followRepository.countFollowing(user.getId()));
        });

        return users;
    }

    public Page<CommentResponseDTO> searchComments(SearchCriteriaModel criteria, Pageable pageable) {
        return commentRepository.searchComments(criteria.getSearchTerm(), pageable)
                .map(comment -> {
                    CommentResponseDTO dto = new CommentResponseDTO();
                    dto.setId(comment.getId());
                    dto.setUserId(comment.getUser().getId());
                    dto.setUsername(comment.getUser().getUsername());
                    dto.setContent(comment.getContent());
                    dto.setCreatedAt(comment.getCreatedAt());
                    return dto;
                });
    }
}