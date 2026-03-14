package com.radiadorespinheiro.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {
    private BigDecimal totalRevenue;
    private BigDecimal totalExpenses;
    private BigDecimal estimatedProfit;
    private BigDecimal averageMargin;
    private List<ProductRankingItem> bestSellingProducts;
    private List<ProductRankingItem> mostProfitableProducts;
    private List<LowStockItem> productsBelowMinStock;
}