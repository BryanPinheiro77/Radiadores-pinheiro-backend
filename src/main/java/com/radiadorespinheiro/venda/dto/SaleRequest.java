package com.radiadorespinheiro.venda.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record SaleRequest(
        @NotBlank String customerName,
        String notes,
        @NotEmpty List<SaleItemRequest> items
) {}