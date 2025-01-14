package com.example.some.controllers;

import com.example.some.dto.comments.CommentResponseDTO;
import com.example.some.dto.posts.PostResponseDTO;
import com.example.some.dto.users.UserResponseDTO;
import com.example.some.dto.users.UserSearchDTO;
import com.example.some.models.search.SearchCriteriaModel;
import com.example.some.services.SearchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
public class SearchController {
    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/posts")
    public ResponseEntity<Page<PostResponseDTO>> searchPosts(
            @RequestParam String query,
            Pageable pageable) {
        SearchCriteriaModel criteria = new SearchCriteriaModel();
        criteria.setSearchTerm(query);
        return ResponseEntity.ok(searchService.searchPosts(criteria, pageable));
    }

    @GetMapping("/users")
    public ResponseEntity<Page<UserSearchDTO>> searchUsers(
            @RequestParam String query,
            Pageable pageable) {
        SearchCriteriaModel criteria = new SearchCriteriaModel();
        criteria.setSearchTerm(query);
        return ResponseEntity.ok(searchService.searchUsers(criteria, pageable));
    }

    @GetMapping("/comments")
    public ResponseEntity<Page<CommentResponseDTO>> searchComments(
            @RequestParam String query,
            Pageable pageable) {
        SearchCriteriaModel criteria = new SearchCriteriaModel();
        criteria.setSearchTerm(query);
        return ResponseEntity.ok(searchService.searchComments(criteria, pageable));
    }
}