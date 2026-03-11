package com.radiadorespinheiro.venda.dto;

import com.radiadorespinheiro.venda.domain.ItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record SaleItemRequest(
        @NotNull ItemType itemType,
        Long productId,
        @NotBlank String description,
        @NotNull Integer quantity,
        @NotNull BigDecimal unitPrice
) {}