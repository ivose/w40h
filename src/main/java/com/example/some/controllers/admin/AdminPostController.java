package com.example.some.controllers.admin;


import com.example.some.dto.SuccessMessageDTO;
import com.example.some.dto.posts.PostDetailsResponseDTO;
import com.example.some.dto.posts.PostResponseDTO;
import com.example.some.dto.posts.PostUpdateRequestDTO;
import com.example.some.models.search.SearchCriteriaModel;
import com.example.some.services.PostService;
import com.example.some.util.constants.MessageConstants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/posts")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPostController {
    private final PostService postService;

    public AdminPostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<Page<PostResponseDTO>> getAllPosts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String sortBy,
            Pageable pageable) {
        SearchCriteriaModel criteria = new SearchCriteriaModel();
        criteria.setSearchTerm(search);
        criteria.setUserIds(userId != null ? List.of(userId) : null);
        criteria.setSortBy(sortBy);
        return ResponseEntity.ok(postService.searchPosts(criteria, pageable));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailsResponseDTO> getPostDetails(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPostDetails(postId));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<SuccessMessageDTO> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.ok(new SuccessMessageDTO(MessageConstants.POST_DELETED));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponseDTO> updatePost(
            @PathVariable Long postId,
            @RequestBody PostUpdateRequestDTO request) {
        return ResponseEntity.ok(postService.updatePost(postId, request));
    }
}