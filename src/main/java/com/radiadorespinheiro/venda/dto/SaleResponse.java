package com.radiadorespinheiro.venda.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SaleResponse(
        Long id,
        String customerName,
        LocalDateTime saleDate,
        BigDecimal totalAmount,
        String notes,
        List<SaleItemResponse> items
) {}