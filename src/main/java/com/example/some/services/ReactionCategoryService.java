package com.example.some.services;

import com.example.some.dto.reactioncategories.ReactionCategoryCreateRequestDTO;
import com.example.some.dto.reactioncategories.ReactionCategoryResponseDTO;
import com.example.some.dto.reactioncategories.ReactionCategoryUpdateRequestDTO;
import com.example.some.entities.ReactionCategory;
import com.example.some.repositories.ReactionCategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReactionCategoryService {
    private final ReactionCategoryRepository categoryRepository;

    public ReactionCategoryService(ReactionCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<ReactionCategoryResponseDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertToCategoryResponse)
                .collect(Collectors.toList());
    }

    public ReactionCategoryResponseDTO getCategory(Long id) {
        return categoryRepository.findById(id)
                .map(this::convertToCategoryResponse)
                .orElseThrow(() -> new RuntimeException("Reaction category not found"));
    }

    private ReactionCategoryResponseDTO convertToCategoryResponse(ReactionCategory category) {
        ReactionCategoryResponseDTO dto = new ReactionCategoryResponseDTO(
                category.getId(),
                category.getName(),
                category.getIcon()
        );
        return dto;
    }

    public Page<ReactionCategoryResponseDTO> searchCategories(String search, Pageable pageable) {
        if (search != null && !search.trim().isEmpty()) {
            return categoryRepository.findByNameContainingIgnoreCase(search.trim(), pageable)
                    .map(this::convertToCategoryResponse);
        }
        return categoryRepository.findAll(pageable)
                .map(this::convertToCategoryResponse);
    }

    public ReactionCategoryResponseDTO createCategory(ReactionCategoryCreateRequestDTO request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new RuntimeException("Reaction category with this name already exists");
        }

        ReactionCategory category = new ReactionCategory();
        category.setName(request.getName());
        category.setIcon(request.getIcon());

        return convertToCategoryResponse(categoryRepository.save(category));
    }

    public ReactionCategoryResponseDTO updateCategory(Long categoryId, ReactionCategoryUpdateRequestDTO request) {
        ReactionCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Reaction category not found"));

        if (request.getName() != null && !request.getName().equals(category.getName())) {
            if (categoryRepository.existsByName(request.getName())) {
                throw new RuntimeException("Reaction category with this name already exists");
            }
            category.setName(request.getName());
        }

        if (request.getIcon() != null) {
            category.setIcon(request.getIcon());
        }

        return convertToCategoryResponse(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        ReactionCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Reaction category not found"));

        if (!category.getReactions().isEmpty()) {
            throw new RuntimeException("Cannot delete category that has associated reactions");
        }

        categoryRepository.delete(category);
    }


}