package com.radiadorespinheiro.sale.service;

import com.radiadorespinheiro.common.exception.BusinessException;
import com.radiadorespinheiro.product.domain.Product;
import com.radiadorespinheiro.product.repository.ProductRepository;
import com.radiadorespinheiro.sale.domain.ItemType;
import com.radiadorespinheiro.sale.domain.Sale;
import com.radiadorespinheiro.sale.domain.SaleItem;
import com.radiadorespinheiro.sale.dto.*;
import com.radiadorespinheiro.sale.repository.SaleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private SaleService saleService;

    private Product product;
    private Sale sale;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("Radiador Ford Ka")
                .costPrice(new BigDecimal("150.00"))
                .salePrice(new BigDecimal("280.00"))
                .stock(10)
                .minStock(3)
                .active(true)
                .build();

        SaleItem item = SaleItem.builder()
                .id(1L)
                .product(product)
                .description("Radiador Ford Ka")
                .quantity(1)
                .unitPrice(new BigDecimal("280.00"))
                .totalPrice(new BigDecimal("280.00"))
                .itemType(ItemType.PRODUCT)
                .build();

        sale = Sale.builder()
                .id(1L)
                .customerName("Fernando")
                .saleDate(LocalDateTime.now())
                .subtotal(new BigDecimal("280.00"))
                .totalAmount(new BigDecimal("280.00"))
                .items(List.of(item))
                .build();
    }

    @Test
    void create_ShouldDeductStock_WhenProductItem() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(saleRepository.save(any())).thenReturn(sale);

        SaleItemRequest itemRequest = new SaleItemRequest(
                ItemType.PRODUCT, 1L, "Radiador Ford Ka", 1, new BigDecimal("280.00"));
        SaleRequest request = new SaleRequest("Fernando", null, null, null, List.of(itemRequest));

        saleService.create(request);

        assertEquals(9, product.getStock()); // stock era 10, deve ser 9
        verify(productRepository).save(product);
    }

    @Test
    void create_ShouldThrowException_WhenInsufficientStock() {
        product.setStock(0);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        SaleItemRequest itemRequest = new SaleItemRequest(
                ItemType.PRODUCT, 1L, "Radiador Ford Ka", 1, new BigDecimal("280.00"));
        SaleRequest request = new SaleRequest("Fernando", null, null, null, List.of(itemRequest));

        assertThrows(BusinessException.class, () -> saleService.create(request));
    }

    @Test
    void create_ShouldThrowException_WhenBothDiscountsProvided() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        SaleItemRequest itemRequest = new SaleItemRequest(
                ItemType.PRODUCT, 1L, "Radiador", 1, new BigDecimal("280.00"));
        SaleRequest request = new SaleRequest(
                "Fernando", null,
                new BigDecimal("10.00"), new BigDecimal("10.00"),
                List.of(itemRequest));

        assertThrows(BusinessException.class, () -> saleService.create(request));
    }

    @Test
    void delete_ShouldRestoreStock_WhenProductItem() {
        when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));

        saleService.delete(1L);

        assertEquals(11, product.getStock()); // stock era 10, deve ser 11
        verify(saleRepository).deleteById(1L); // troca delete por deleteById
    }

    @Test
    void delete_ShouldThrowException_WhenNotFound() {
        when(saleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> saleService.delete(99L));
    }

    @Test
    void findById_ShouldThrowException_WhenNotFound() {
        when(saleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> saleService.findById(99L));
    }
}