package com.radiadorespinheiro.categoria.service;

import com.radiadorespinheiro.categoria.domain.Category;
import com.radiadorespinheiro.categoria.dto.CategoryRequest;
import com.radiadorespinheiro.categoria.dto.CategoryResponse;
import com.radiadorespinheiro.categoria.repository.CategoryRepository;
import com.radiadorespinheiro.common.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CategoryResponse create(CategoryRequest request) {
        if(categoryRepository.existsByName(request.name())){
            throw new BusinessException("Category with name '" + request.name() + "' already exists.");
        }
        Category category = Category.builder()
                .name(request.name())
                .description(request.description())
                .build();
        return toResponse(categoryRepository.save(category));
    }

    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public CategoryResponse findById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = findOrThrow(id);
        category.setName(request.name());
        category.setDescription(request.description());
        return toResponse(categoryRepository.save(category));
    }

    public CategoryResponse patch(Long id, CategoryRequest request) {
        Category category = findOrThrow(id);
        if (request.name() != null && !request.name().isBlank()) {
            category.setName(request.name());
        }
        if (request.description() != null) {
            category.setDescription(request.description());
        }
        return toResponse(categoryRepository.save(category));
    }

    public void delete(Long id) {
        findOrThrow(id);
        categoryRepository.deleteById(id);
    }
    private Category findOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Category with id '" + id + "' not found."));
    }
    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getDescription());
    }
}
