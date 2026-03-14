package com.radiadorespinheiro.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRankingItem {
    private Long productId;
    private String productName;
    private Integer totalQuantitySold;
    private BigDecimal totalRevenue;
    private BigDecimal totalProfit;
}