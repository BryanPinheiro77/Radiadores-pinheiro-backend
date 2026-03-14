package com.radiadorespinheiro.sale.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SaleResponse(
        Long id,
        String customerName,
        LocalDateTime saleDate,
        BigDecimal subtotal,
        BigDecimal discountValue,
        BigDecimal discountPercentual,
        BigDecimal totalAmount,
        String notes,
        List<SaleItemResponse> items
) {}