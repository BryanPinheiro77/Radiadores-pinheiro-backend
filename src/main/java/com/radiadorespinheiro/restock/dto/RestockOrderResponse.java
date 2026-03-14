package com.radiadorespinheiro.restock.dto;

import java.time.LocalDateTime;
import java.util.List;

public record RestockOrderResponse(
        Long id,
        LocalDateTime createdAt,
        String notes,
        List<RestockOrderItemResponse> items
) {}