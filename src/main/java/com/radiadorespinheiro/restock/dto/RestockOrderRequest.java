package com.radiadorespinheiro.restock.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record RestockOrderRequest(
        String notes,
        @NotEmpty List<RestockOrderItemRequest> items
) {}