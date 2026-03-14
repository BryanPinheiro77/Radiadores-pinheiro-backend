package com.radiadorespinheiro.product.service;

import com.radiadorespinheiro.category.domain.Category;
import com.radiadorespinheiro.category.repository.CategoryRepository;
import com.radiadorespinheiro.common.exception.BusinessException;
import com.radiadorespinheiro.product.domain.Product;
import com.radiadorespinheiro.product.dto.ProductRequest;
import com.radiadorespinheiro.product.dto.ProductResponse;
import com.radiadorespinheiro.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private Category category;

    @BeforeEach
    void setUp() {
        category = Category.builder().id(1L).name("Radiadores").build();
        product = Product.builder()
                .id(1L)
                .name("Radiador Ford Ka")
                .costPrice(new BigDecimal("150.00"))
                .salePrice(new BigDecimal("280.00"))
                .stock(10)
                .minStock(3)
                .active(true)
                .category(category)
                .build();
    }

    @Test
    void create_ShouldReturnResponse_WhenValid() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any())).thenReturn(product);

        ProductRequest request = new ProductRequest(
                "Radiador Ford Ka", null,
                new BigDecimal("150.00"), new BigDecimal("280.00"),
                10, 3, 1L);

        ProductResponse response = productService.create(request);

        assertNotNull(response);
        assertEquals("Radiador Ford Ka", response.name());
        verify(productRepository).save(any());
    }

    @Test
    void findAll_ShouldReturnList() {
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<ProductResponse> result = productService.findAll();

        assertEquals(1, result.size());
        assertEquals("Radiador Ford Ka", result.get(0).name());
    }

    @Test
    void findById_ShouldReturnResponse_WhenExists() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponse response = productService.findById(1L);

        assertEquals(1L, response.id());
        assertEquals("Radiador Ford Ka", response.name());
    }

    @Test
    void findById_ShouldThrowException_WhenNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> productService.findById(99L));
    }

    @Test
    void toggleActive_ShouldFlipActiveStatus() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenReturn(product);

        productService.toggleActive(1L);

        verify(productRepository).save(any());
    }

    @Test
    void delete_ShouldCallRepository_WhenExists() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.delete(1L);

        verify(productRepository).deleteById(1L);
    }
}