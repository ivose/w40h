package com.example.some.services;

import com.example.some.dto.users.UserCreateRequestDTO;
import com.example.some.dto.users.UserUpdateRequestDTO;
import com.example.some.dto.users.UserResponseDTO;
import com.example.some.dto.users.UserDetailsResponseDTO;
import com.example.some.entities.User;
import com.example.some.models.search.SearchCriteriaModel;
import com.example.some.repositories.UserRepository;
import com.example.some.repositories.FollowRepository;
import com.example.some.util.security.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Value("${app.frontend.url}/reset-password")
    private String resetPasswordUrl;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

//    public UserService(UserRepository userRepository,
//                       FollowRepository followRepository,
//                       PasswordEncoder passwordEncoder) {
//        this.userRepository = userRepository;
//        this.followRepository = followRepository;
//        this.passwordEncoder = passwordEncoder;
//    }

    public UserResponseDTO createUser(UserCreateRequestDTO request) {
        validateNewUser(request);

        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullname());
        user.setBorn(request.getBorn());
        user.setActive(false); // Set to false until email verification

        String confirmationToken = TokenUtils.generateToken();
        user.setResetToken(confirmationToken);
        user.setResetTokenExpiry(LocalDateTime.now().plusDays(1));

        //User savedUser = userRepository.save(user);

        // Send confirmation email
        String confirmationLink = resetPasswordUrl.replace("/reset-password", "/confirm-email")
                + "?token=" + confirmationToken;
        emailService.sendEmail(
                user.getEmail(),
                "Confirm Your Email",
                "Welcome! Please confirm your email by clicking this link: " + confirmationLink
        );

        User savedUser = userRepository.save(user);
        return convertToUserResponse(savedUser);
    }

    public UserDetailsResponseDTO getUserDetails(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetailsResponseDTO response = new UserDetailsResponseDTO();
        // Set all fields
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFullname(user.getFullName());
        response.setBorn(user.getBorn());
        response.setActive(user.isActive());
        response.setAdmin(user.isAdmin());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());

        // Add counts
        response.setFollowersCount((int) followRepository.countFollowers(userId));
        response.setFollowingCount((int) followRepository.countFollowing(userId));

        return response;
    }

    public UserResponseDTO updateUser(Long userId, UserUpdateRequestDTO request, Long myId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getFullname() != null) {
            user.setFullName(request.getFullname());
        }
        if (request.getBorn() != null) {
            user.setBorn(request.getBorn());
        }
        if (request.getNewPassword() != null) {
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new RuntimeException("Current password is incorrect");
            }
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        return convertToUserResponse(userRepository.save(user));
    }

    private void validateNewUser(UserCreateRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
    }

    public UserResponseDTO convertToUserResponse(User user) {
        UserResponseDTO response = new UserResponseDTO();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFullname(user.getFullName());
        response.setBorn(user.getBorn());
        response.setActive(user.isActive());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }


    // For admin,
    public void deleteUser(Long userId, SearchCriteriaModel criteria, Long myId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if(userId == myId) {
            throw new RuntimeException("You cannot delete yourself");
        }
        userRepository.delete(user);
    }

    public UserResponseDTO updateUserStatus(Long userId, boolean active, SearchCriteriaModel criteria, Long myId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if(userId == myId) {
            throw new RuntimeException("You cannot change your own status here");
        }
        user.setActive(active);
        return convertToUserResponse(userRepository.save(user));
    }

    public UserResponseDTO updateUserRole(Long userId, boolean isAdmin, SearchCriteriaModel criteria, Long myId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if(userId == myId) {
            throw new RuntimeException("You cannot change your own role here");
        }
        user.setAdmin(isAdmin);
        return convertToUserResponse(userRepository.save(user));
    }

    //public Page<UserResponseDTO> searchUsers(SearchCriteriaModel criteria, Pageable pageable, long userId) {
    //    return userRepository.searchUsersExcluding(criteria.getSearchTerm(), userId, pageable)
    //            .map(this::convertToUserResponse);
    //}

    public Page<UserResponseDTO> searchUsers(SearchCriteriaModel criteria, Pageable pageable, long userId) {
        if (criteria != null && criteria.getSearchTerm() != null && !criteria.getSearchTerm().trim().isEmpty()) {
            // If search term is provided, use search
            return userRepository.searchUsersExcluding(criteria.getSearchTerm(), userId, pageable)
                    .map(this::convertToUserResponse);
        }
        // If no search term, return all users except current user
        return userRepository.findByIdNot(userId, pageable)
                .map(this::convertToUserResponse);
    }
}