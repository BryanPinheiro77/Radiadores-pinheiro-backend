package com.radiadorespinheiro.product.dto;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal costPrice,
        BigDecimal salePrice,
        Integer stock,
        Integer minStock,
        Boolean active,
        String categoryName
) {}