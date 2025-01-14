package com.example.some.controllers.admin;

import com.example.some.dto.SuccessMessageDTO;
import com.example.some.dto.users.*;
import com.example.some.models.search.SearchCriteriaModel;
import com.example.some.services.UserService;
import com.example.some.util.constants.MessageConstants;
import com.example.some.util.security.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {
    private final UserService userService;
    private final SecurityUtils securityUtils;

    public AdminUserController(UserService userService, SecurityUtils securityUtils)
    {
        this.userService = userService;
        this.securityUtils = securityUtils;
    }

    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean active,
            Pageable pageable) {
        SearchCriteriaModel criteria = new SearchCriteriaModel();
        criteria.setSearchTerm(search);
        criteria.setIncludeInactive(active != null ? !active : true);

        return ResponseEntity.ok(userService.searchUsers(criteria, pageable, securityUtils.getCurrentUserId()));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDetailsResponseDTO> getUserDetails(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserDetails(userId));
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserCreateRequestDTO request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long userId,
            @RequestBody UserUpdateRequestDTO request) {
        return ResponseEntity.ok(userService.updateUser(userId, request, securityUtils.getCurrentUserId()));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<SuccessMessageDTO> deleteUser(
            @PathVariable Long userId) {
        SearchCriteriaModel criteria = new SearchCriteriaModel();
        userService.deleteUser(userId, criteria, securityUtils.getCurrentUserId());
        return ResponseEntity.ok(new SuccessMessageDTO(MessageConstants.USER_DELETED));
    }

    @PutMapping("/{userId}/status")
    public ResponseEntity<UserResponseDTO> updateUserStatus(
            @PathVariable Long userId,
            @RequestParam boolean active) {
        SearchCriteriaModel criteria = new SearchCriteriaModel();
        return ResponseEntity.ok(userService.updateUserStatus(userId, active, criteria, securityUtils.getCurrentUserId()));
    }

    @PutMapping("/{userId}/admin")
    public ResponseEntity<UserResponseDTO> updateUserAdmin(
            @PathVariable Long userId,
            @RequestParam boolean isAdmin) {
        SearchCriteriaModel criteria = new SearchCriteriaModel();
        return ResponseEntity.ok(userService.updateUserRole(userId, isAdmin, criteria, securityUtils.getCurrentUserId()));
    }
}