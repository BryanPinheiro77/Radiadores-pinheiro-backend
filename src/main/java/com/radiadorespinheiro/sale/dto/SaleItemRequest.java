package com.radiadorespinheiro.sale.dto;

import com.radiadorespinheiro.sale.domain.ItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.DecimalMin;

public record SaleItemRequest(
        @NotNull ItemType itemType,
        Long productId,
        @NotBlank String description,
        @NotNull @Min(value = 1, message = "Quantity must be at least 1") Integer quantity,
        @NotNull @DecimalMin(value = "0.01", message = "Unit price must be greater than zero") BigDecimal unitPrice
) {}