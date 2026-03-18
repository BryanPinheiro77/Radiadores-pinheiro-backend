package com.radiadorespinheiro.report.service;

import com.radiadorespinheiro.expense.repository.ExpenseRepository;
import com.radiadorespinheiro.product.domain.Product;
import com.radiadorespinheiro.product.repository.ProductRepository;
import com.radiadorespinheiro.report.dto.LowStockItem;
import com.radiadorespinheiro.report.dto.ProductRankingItem;
import com.radiadorespinheiro.report.dto.ReportResponse;
import com.radiadorespinheiro.sale.domain.ItemType;
import com.radiadorespinheiro.sale.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
        return saleRepository.sumTotalAmountBetween(start, end);
    }

    private BigDecimal getTotalExpenses(LocalDate start, LocalDate end) {
        return expenseRepository.sumValueBetween(start, end);
    }

    private BigDecimal getTotalCost(LocalDateTime start, LocalDateTime end) {
        return saleRepository.sumTotalCostBetween(start, end);
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

    public Map<String, BigDecimal> getTotalBalance() {
        BigDecimal totalRevenue = saleRepository.sumTotalAmount();
        BigDecimal totalExpenses = expenseRepository.sumValue();
        BigDecimal balance = totalRevenue.subtract(totalExpenses);
        return Map.of(
                "totalRevenue", totalRevenue,
                "totalExpenses", totalExpenses,
                "balance", balance
        );
    }

    public List<Map<String, Object>> getCategoryRevenue(LocalDate start, LocalDate end) {
        LocalDateTime startDt = start.atStartOfDay();
        LocalDateTime endDt = end.atTime(23, 59, 59);

        return saleRepository.findBySaleDateBetween(startDt, endDt)
                .stream()
                .flatMap(sale -> sale.getItems().stream())
                .filter(item -> {
                    if (item.getCategory() != null) return true;
                    if (item.getProduct() != null && item.getProduct().getCategory() != null) return true;
                    return false;
                })
                .collect(java.util.stream.Collectors.groupingBy(
                        item -> {
                            if (item.getCategory() != null) return item.getCategory().getName();
                            return item.getProduct().getCategory().getName();
                        },
                        java.util.stream.Collectors.toList()
                ))
                .entrySet().stream()
                .map(entry -> {
                    String categoryName = entry.getKey();
                    var items = entry.getValue();
                    BigDecimal totalRevenue = items.stream()
                            .map(i -> i.getTotalPrice())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    Map<String, Object> result = new java.util.HashMap<>();
                    result.put("categoryName", categoryName);
                    result.put("totalRevenue", totalRevenue);
                    return result;
                })
                .sorted((a, b) -> ((BigDecimal) b.get("totalRevenue"))
                        .compareTo((BigDecimal) a.get("totalRevenue")))
                .toList();
    }

    public List<Map<String, Object>> getCategoryExpenses(LocalDate start, LocalDate end) {
        return expenseRepository.findAllByDateBetween(start, end)
                .stream()
                .filter(expense -> expense.getCategory() != null)
                .collect(java.util.stream.Collectors.groupingBy(
                        expense -> expense.getCategory().getName(),
                        java.util.stream.Collectors.toList()
                ))
                .entrySet().stream()
                .map(entry -> {
                    String categoryName = entry.getKey();
                    var expenses = entry.getValue();
                    BigDecimal totalExpenses = expenses.stream()
                            .map(e -> e.getValue())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    Map<String, Object> result = new java.util.HashMap<>();
                    result.put("categoryName", categoryName);
                    result.put("totalExpenses", totalExpenses);
                    return result;
                })
                .sorted((a, b) -> ((BigDecimal) b.get("totalExpenses"))
                        .compareTo((BigDecimal) a.get("totalExpenses")))
                .toList();
    }

    public List<Map<String, Object>> getCategoryProfit(LocalDate start, LocalDate end) {
        LocalDateTime startDt = start.atStartOfDay();
        LocalDateTime endDt = end.atTime(23, 59, 59);

        return saleRepository.findBySaleDateBetween(startDt, endDt)
                .stream()
                .flatMap(sale -> sale.getItems().stream())
                .filter(item -> {
                    if (item.getCategory() != null) return true;
                    if (item.getProduct() != null && item.getProduct().getCategory() != null) return true;
                    return false;
                })
                .collect(java.util.stream.Collectors.groupingBy(
                        item -> {
                            if (item.getCategory() != null) return item.getCategory().getName();
                            return item.getProduct().getCategory().getName();
                        },
                        java.util.stream.Collectors.toList()
                ))
                .entrySet().stream()
                .map(entry -> {
                    String categoryName = entry.getKey();
                    var items = entry.getValue();
                    BigDecimal totalRevenue = items.stream()
                            .map(i -> i.getTotalPrice())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal totalCost = items.stream()
                            .map(i -> {
                                if (i.getItemType() == ItemType.SERVICE && i.getServiceCost() != null) {
                                    return i.getServiceCost().multiply(BigDecimal.valueOf(i.getQuantity()));
                                }
                                if (i.getProduct() != null) {
                                    return i.getProduct().getCostPrice()
                                            .multiply(BigDecimal.valueOf(i.getQuantity()));
                                }
                                return BigDecimal.ZERO;
                            })
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal totalProfit = totalRevenue.subtract(totalCost);
                    Map<String, Object> result = new java.util.HashMap<>();
                    result.put("categoryName", categoryName);
                    result.put("totalRevenue", totalRevenue);
                    result.put("totalProfit", totalProfit);
                    return result;
                })
                .sorted((a, b) -> ((BigDecimal) b.get("totalProfit"))
                        .compareTo((BigDecimal) a.get("totalProfit")))
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