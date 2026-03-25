package com.radiadorespinheiro.dashboard.dto;

import com.radiadorespinheiro.report.dto.ReportResponse;

import java.math.BigDecimal;
import java.util.List;

public record DashboardSummaryResponse(
        ReportResponse currentMonthReport,
        BigDecimal totalRevenue,
        BigDecimal totalExpenses,
        BigDecimal balance,
        List<MonthlyChartPoint> monthlyChart
) {
}