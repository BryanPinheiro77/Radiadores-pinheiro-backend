package com.radiadorespinheiro.category.service;

import com.radiadorespinheiro.category.domain.Category;
import com.radiadorespinheiro.category.dto.CategoryRequest;
import com.radiadorespinheiro.category.dto.CategoryResponse;
import com.radiadorespinheiro.category.repository.CategoryRepository;
import com.radiadorespinheiro.common.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id(1L)
                .name("Radiadores")
                .description("Categoria de radiadores")
                .build();
    }

    @Test
    void create_ShouldReturnResponse_WhenNameIsUnique() {
        when(categoryRepository.existsByName("Radiadores")).thenReturn(false);
        when(categoryRepository.save(any())).thenReturn(category);

        CategoryResponse response = categoryService.create(new CategoryRequest("Radiadores", "Categoria de radiadores"));

        assertNotNull(response);
        assertEquals("Radiadores", response.name());
        verify(categoryRepository).save(any());
    }

    @Test
    void create_ShouldThrowException_WhenNameAlreadyExists() {
        when(categoryRepository.existsByName("Radiadores")).thenReturn(true);

        assertThrows(BusinessException.class, () ->
                categoryService.create(new CategoryRequest("Radiadores", "desc")));

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void findAll_ShouldReturnList() {
        when(categoryRepository.findAll()).thenReturn(List.of(category));

        List<CategoryResponse> result = categoryService.findAll();

        assertEquals(1, result.size());
        assertEquals("Radiadores", result.get(0).name());
    }

    @Test
    void findById_ShouldReturnResponse_WhenExists() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        CategoryResponse response = categoryService.findById(1L);

        assertEquals(1L, response.id());
        assertEquals("Radiadores", response.name());
    }

    @Test
    void findById_ShouldThrowException_WhenNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> categoryService.findById(99L));
    }

    @Test
    void delete_ShouldCallRepository_WhenExists() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        categoryService.delete(1L);

        verify(categoryRepository).deleteById(1L);
    }

    @Test
    void delete_ShouldThrowException_WhenNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> categoryService.delete(99L));
        verify(categoryRepository, never()).deleteById(any());
    }
}