package com.radiadorespinheiro.venda.dto;

import com.radiadorespinheiro.venda.domain.ItemType;
import java.math.BigDecimal;

public record SaleItemResponse(
        Long id,
        ItemType itemType,
        String description,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice
) {}