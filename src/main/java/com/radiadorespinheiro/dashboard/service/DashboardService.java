package com.radiadorespinheiro.dashboard.service;

import com.radiadorespinheiro.dashboard.dto.DashboardSummaryResponse;
import com.radiadorespinheiro.dashboard.dto.MonthlyChartPoint;
import com.radiadorespinheiro.expense.repository.ExpenseRepository;
import com.radiadorespinheiro.report.dto.ReportResponse;
import com.radiadorespinheiro.report.service.ReportService;
import com.radiadorespinheiro.sale.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ReportService reportService;
    private final SaleRepository saleRepository;
    private final ExpenseRepository expenseRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "dashboard-summary", key = "#start + '-' + #end")
    public DashboardSummaryResponse getSummary(LocalDate start, LocalDate end) {
        ReportResponse currentMonthReport = reportService.getReport(start, end);

        BigDecimal totalRevenue = saleRepository.sumTotalAmount();
        BigDecimal totalExpenses = expenseRepository.sumValue();
        BigDecimal balance = totalRevenue.subtract(totalExpenses);

        List<MonthlyChartPoint> monthlyChart = buildLastSixMonthsChart();

        return new DashboardSummaryResponse(
                currentMonthReport,
                totalRevenue,
                totalExpenses,
                balance,
                monthlyChart
        );
    }

    private List<MonthlyChartPoint> buildLastSixMonthsChart() {
        LocalDate today = LocalDate.now();

        return IntStream.rangeClosed(0, 5)
                .map(i -> 5 - i)
                .mapToObj(offset -> {
                    LocalDate monthStart = today.minusMonths(offset).withDayOfMonth(1);
                    LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());

                    LocalDateTime startDt = monthStart.atStartOfDay();
                    LocalDateTime endDt = monthEnd.atTime(23, 59, 59);

                    BigDecimal faturamento = saleRepository.sumTotalAmountBetween(startDt, endDt);
                    BigDecimal despesas = expenseRepository.sumValueBetween(monthStart, monthEnd);

                    return new MonthlyChartPoint(
                            formatMonthLabel(monthStart),
                            faturamento,
                            despesas
                    );
                })
                .toList();
    }

    private String formatMonthLabel(LocalDate date) {
        String month = date.getMonth().getDisplayName(TextStyle.SHORT, new Locale("pt", "BR"));
        String year = String.valueOf(date.getYear()).substring(2);
        return month + "/" + year;
    }
}