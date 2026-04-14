package com.radiadorespinheiro.product.dto;

import java.math.BigDecimal;

public record ProductPatchRequest(
        String name,
        String description,
        BigDecimal costPrice,
        BigDecimal salePrice,
        Integer stock,
        Integer minStock,
        Long categoryId,
        Boolean active
) {
}