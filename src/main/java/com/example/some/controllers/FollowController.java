package com.example.some.controllers;

import com.example.some.dto.SuccessMessageDTO;
import com.example.some.dto.follows.*;
import com.example.some.dto.users.UserResponseDTO;
import com.example.some.services.FollowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RestController
@RequestMapping("/api/follow")
public class FollowController {
    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping
    public ResponseEntity<FollowResponseDTO> followUser(
            @RequestAttribute("userId") Long userId,
            @RequestBody FollowRequestDTO request) {
        return ResponseEntity.ok(followService.followUser(userId, request));
    }

    @DeleteMapping("/{followeeId}")
    public ResponseEntity<SuccessMessageDTO> unfollowUser(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long followeeId) {
        followService.unfollowUser(userId, followeeId);
        return ResponseEntity.ok(new SuccessMessageDTO("Unfollowed successfully"));
    }

    @GetMapping("/followers/{userId}")
    public ResponseEntity<Page<UserResponseDTO>> getFollowers(
            @PathVariable Long userId,
            Pageable pageable) {
        return ResponseEntity.ok(followService.getFollowers(userId, pageable));
    }

    @GetMapping("/following/{userId}")
    public ResponseEntity<Page<UserResponseDTO>> getFollowing(
            @PathVariable Long userId,
            Pageable pageable) {
        return ResponseEntity.ok(followService.getFollowing(userId, pageable));
    }
}