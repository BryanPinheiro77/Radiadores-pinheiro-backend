package com.radiadorespinheiro.restock.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RestockOrderItemRequest(
        @NotNull Long productId,
        @NotNull Integer suggestedQuantity,
        @NotNull @Min(value = 1, message = "Ordered quantity must be at least 1") Integer orderedQuantity
) {}