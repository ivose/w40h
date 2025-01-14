package com.example.some.controllers.admin;

import com.example.some.dto.SuccessMessageDTO;
import com.example.some.dto.reactioncategories.*;
import com.example.some.services.ReactionCategoryService;
import com.example.some.util.constants.MessageConstants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/reaction-categories")
@PreAuthorize("hasRole('ADMIN')")
public class AdminReactionCategoryController {
    private final ReactionCategoryService categoryService;

    public AdminReactionCategoryController(ReactionCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<Page<ReactionCategoryResponseDTO>> getAllCategories(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return ResponseEntity.ok(categoryService.searchCategories(search, pageable));
    }

    @PostMapping
    public ResponseEntity<ReactionCategoryResponseDTO> createCategory(
            @RequestBody ReactionCategoryCreateRequestDTO request) {
        return ResponseEntity.ok(categoryService.createCategory(request));
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<ReactionCategoryResponseDTO> updateCategory(
            @PathVariable Long categoryId,
            @RequestBody ReactionCategoryUpdateRequestDTO request) {
        return ResponseEntity.ok(categoryService.updateCategory(categoryId, request));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<SuccessMessageDTO> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok(new SuccessMessageDTO(MessageConstants.CATEGORY_DELETED));
    }
}