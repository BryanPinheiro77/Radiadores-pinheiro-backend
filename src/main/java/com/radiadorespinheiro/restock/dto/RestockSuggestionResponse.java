package com.radiadorespinheiro.restock.dto;

public record RestockSuggestionResponse(
        Long productId,
        String productName,
        String categoryName,
        Integer currentStock,
        Integer minStock,
        Integer suggestedQuantity
) {}