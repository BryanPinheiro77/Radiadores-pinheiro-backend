package com.radiadorespinheiro.relatorio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LowStockItem {
    private Long productId;
    private String productName;
    private Integer currentStock;
    private Integer minimumStock;
}