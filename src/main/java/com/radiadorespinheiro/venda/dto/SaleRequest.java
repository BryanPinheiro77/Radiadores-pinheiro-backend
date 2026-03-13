package com.radiadorespinheiro.venda.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;

public record SaleRequest(
        @NotBlank String customerName,
        String notes,
        @DecimalMin(value = "0.0", inclusive = false, message = "Discount must be greater than zero")
        BigDecimal discountValue,
        @DecimalMin(value = "0.0", inclusive = false, message = "Discount must be greater than zero")
        @DecimalMax(value = "100.0", message = "Discount percentage cannot exceed 100")
        BigDecimal discountPercentual,
        @NotEmpty List<SaleItemRequest> items
) {}