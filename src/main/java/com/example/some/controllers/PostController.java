package com.example.some.controllers;

import com.example.some.dto.SuccessMessageDTO;
import com.example.some.dto.posts.*;
import com.example.some.services.PostService;
import com.example.some.util.constants.MessageConstants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<Page<PostResponseDTO>> getAllPosts(
            Pageable pageable) {
        return ResponseEntity.ok(postService.getAllPosts(pageable));
    }

    @GetMapping("/my")
    public ResponseEntity<Page<PostResponseDTO>> getMyPosts(
            @RequestAttribute("userId") Long userId,
            Pageable pageable) {
        return ResponseEntity.ok(postService.getMyPosts(userId, pageable));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PostResponseDTO>> getUserPosts(
            @PathVariable Long userId,
            Pageable pageable) {
        return ResponseEntity.ok(postService.getUserPosts(userId, pageable));
    }

    @GetMapping("/feed")
    public ResponseEntity<Page<PostResponseDTO>> getUserFeed(
            @RequestAttribute("userId") Long userId,
            Pageable pageable) {
        return ResponseEntity.ok(postService.getUserFeed(userId, pageable));
    }

    @PostMapping
    public ResponseEntity<PostResponseDTO> createPost(
            @RequestAttribute("userId") Long userId,
            @RequestBody PostCreateRequestDTO request) {
        return ResponseEntity.ok(postService.createPost(userId, request));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailsResponseDTO> getPostDetails(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPostDetails(postId));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponseDTO> updateMyPost(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long postId,
            @RequestBody PostUpdateRequestDTO request) {
        return ResponseEntity.ok(postService.updateMyPost(userId, postId, request));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<SuccessMessageDTO> deleteMyPost(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long postId) {
        postService.deleteMyPost(userId, postId);
        return ResponseEntity.ok(new SuccessMessageDTO(MessageConstants.POST_DELETED));
    }
}