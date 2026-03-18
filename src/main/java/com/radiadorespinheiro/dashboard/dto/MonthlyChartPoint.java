package com.radiadorespinheiro.dashboard.dto;

import java.math.BigDecimal;

public record MonthlyChartPoint(
        String mes,
        BigDecimal faturamento,
        BigDecimal despesas
) {
}