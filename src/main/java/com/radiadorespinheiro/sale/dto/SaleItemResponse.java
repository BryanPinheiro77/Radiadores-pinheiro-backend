package com.radiadorespinheiro.sale.dto;

import com.radiadorespinheiro.sale.domain.ItemType;
import java.math.BigDecimal;

public record SaleItemResponse(
        Long id,
        ItemType itemType,
        String description,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice,
        String categoryName,
        BigDecimal serviceCost
) {}