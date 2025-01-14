package com.example.some.services;

import com.example.some.dto.follows.FollowRequestDTO;
import com.example.some.dto.follows.FollowResponseDTO;
import com.example.some.dto.users.UserResponseDTO;
import com.example.some.entities.Follow;
import com.example.some.entities.User;
import com.example.some.repositories.FollowRepository;
import com.example.some.repositories.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public FollowService(FollowRepository followRepository,
                         UserRepository userRepository) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
    }

    public FollowResponseDTO followUser(Long followerId, FollowRequestDTO request) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Follower not found"));

        User followee = userRepository.findById(request.getFolloweeId())
                .orElseThrow(() -> new RuntimeException("Followee not found"));

        if (follower.getId().equals(followee.getId())) {
            throw new RuntimeException("Users cannot follow themselves");
        }

        // Check if already following
        if (followRepository.existsByFollowerIdAndFolloweeId(followerId, followee.getId())) {
            throw new RuntimeException("Already following this user");
        }

        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowee(followee);

        Follow savedFollow = followRepository.save(follow);
        return convertToFollowResponse(savedFollow);
    }

    public void unfollowUser(Long followerId, Long followeeId) {
        if (!followRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new RuntimeException("Follow relationship not found");
        }
        followRepository.deleteByFollowerIdAndFolloweeId(followerId, followeeId);
    }

    public Page<UserResponseDTO> getFollowers(Long userId, Pageable pageable) {
        return followRepository.findFollowersByUserId(userId, pageable)
                .map(this::convertToUserResponse);
    }

    public Page<UserResponseDTO> getFollowing(Long userId, Pageable pageable) {
        return followRepository.findFolloweesByUserId(userId, pageable)
                .map(this::convertToUserResponse);
//                stream().map(this::convertToUserResponse)
//                .collect(Collectors.toList());
    }

    private FollowResponseDTO convertToFollowResponse(Follow follow) {
        FollowResponseDTO response = new FollowResponseDTO();
        response.setId(follow.getId());
        response.setFollower(convertToUserResponse(follow.getFollower()));
        response.setFollowee(convertToUserResponse(follow.getFollowee()));
        response.setCreatedAt(follow.getCreatedAt());
        return response;
    }

    private UserResponseDTO convertToUserResponse(User user) {
        UserResponseDTO response = new UserResponseDTO();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFullname(user.getFullName());
        response.setBorn(user.getBorn());
        response.setActive(user.isActive());
        response.setCreatedAt(user.getCreatedAt());
        response.setFollowersCount((int) followRepository.countFollowers(user.getId()));
        response.setFollowingCount((int) followRepository.countFollowing(user.getId()));
        return response;
    }
}