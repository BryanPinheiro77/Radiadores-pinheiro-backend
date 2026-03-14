package com.radiadorespinheiro.restock.dto;

public record RestockOrderItemResponse(
        Long id,
        Long productId,
        String productName,
        String categoryName,
        Integer currentStock,
        Integer suggestedQuantity,
        Integer orderedQuantity
) {}