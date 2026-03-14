package com.radiadorespinheiro.report.service;

import com.radiadorespinheiro.expense.repository.ExpenseRepository;
import com.radiadorespinheiro.product.domain.Product;
import com.radiadorespinheiro.product.repository.ProductRepository;
import com.radiadorespinheiro.report.dto.LowStockItem;
import com.radiadorespinheiro.report.dto.ProductRankingItem;
import com.radiadorespinheiro.report.dto.ReportResponse;
import com.radiadorespinheiro.sale.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final SaleRepository saleRepository;
    private final ExpenseRepository expenseRepository;
    private final ProductRepository productRepository;

    public ReportResponse getReport(LocalDate start, LocalDate end) {
        LocalDateTime startDt = start.atStartOfDay();
        LocalDateTime endDt = end.atTime(23, 59, 59);

        BigDecimal totalRevenue = getTotalRevenue(startDt, endDt);
        BigDecimal totalExpenses = getTotalExpenses(start, end);
        BigDecimal totalCost = getTotalCost(startDt, endDt);
        BigDecimal estimatedProfit = totalRevenue.subtract(totalExpenses).subtract(totalCost);
        BigDecimal averageMargin = totalRevenue.compareTo(BigDecimal.ZERO) > 0
                ? estimatedProfit.divide(totalRevenue, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        return ReportResponse.builder()
                .totalRevenue(totalRevenue)
                .totalExpenses(totalExpenses)
                .estimatedProfit(estimatedProfit)
                .averageMargin(averageMargin)
                .bestSellingProducts(getBestSellingProducts(startDt, endDt))
                .mostProfitableProducts(getMostProfitableProducts(startDt, endDt))
                .productsBelowMinStock(getProductsBelowMinStock())
                .build();
    }

    private BigDecimal getTotalRevenue(LocalDateTime start, LocalDateTime end) {
        return saleRepository.findBySaleDateBetween(start, end)
                .stream()
                .map(sale -> sale.getTotalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getTotalExpenses(LocalDate start, LocalDate end) {
        return expenseRepository.findByDateBetween(start, end)
                .stream()
                .map(expense -> expense.getValue())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getTotalCost(LocalDateTime start, LocalDateTime end) {
        return saleRepository.findBySaleDateBetween(start, end)
                .stream()
                .flatMap(sale -> sale.getItems().stream())
                .filter(item -> item.getProduct() != null)
                .map(item -> item.getProduct().getCostPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<ProductRankingItem> getBestSellingProducts(LocalDateTime start, LocalDateTime end) {
        return saleRepository.findBySaleDateBetween(start, end)
                .stream()
                .flatMap(sale -> sale.getItems().stream())
                .filter(item -> item.getProduct() != null)
                .collect(java.util.stream.Collectors.groupingBy(
                        item -> item.getProduct(),
                        java.util.stream.Collectors.toList()
                ))
                .entrySet().stream()
                .map(entry -> {
                    Product p = entry.getKey();
                    var items = entry.getValue();
                    int totalQty = items.stream().mapToInt(i -> i.getQuantity()).sum();
                    BigDecimal totalRev = items.stream().map(i -> i.getTotalPrice()).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal totalCost = p.getCostPrice().multiply(BigDecimal.valueOf(totalQty));
                    BigDecimal totalProfit = totalRev.subtract(totalCost);
                    return ProductRankingItem.builder()
                            .productId(p.getId())
                            .productName(p.getName())
                            .totalQuantitySold(totalQty)
                            .totalRevenue(totalRev)
                            .totalProfit(totalProfit)
                            .build();
                })
                .sorted((a, b) -> b.getTotalQuantitySold().compareTo(a.getTotalQuantitySold()))
                .limit(10)
                .toList();
    }

    private List<ProductRankingItem> getMostProfitableProducts(LocalDateTime start, LocalDateTime end) {
        return getBestSellingProducts(start, end)
                .stream()
                .sorted((a, b) -> b.getTotalProfit().compareTo(a.getTotalProfit()))
                .toList();
    }

    private List<LowStockItem> getProductsBelowMinStock() {
        return productRepository.findAll()
                .stream()
                .filter(p -> p.getStock() < p.getMinStock())
                .map(p -> LowStockItem.builder()
                        .productId(p.getId())
                        .productName(p.getName())
                        .currentStock(p.getStock())
                        .minimumStock(p.getMinStock())
                        .build())
                .toList();
    }
}