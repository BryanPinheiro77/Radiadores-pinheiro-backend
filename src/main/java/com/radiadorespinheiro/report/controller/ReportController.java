package com.radiadorespinheiro.report.controller;

import com.radiadorespinheiro.report.dto.ReportResponse;
import com.radiadorespinheiro.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Tag(name = "Reports", description = "Financial and operational reports")
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "Get full report for a period (revenue, expenses, profit, top products)")
    @GetMapping
    public ResponseEntity<ReportResponse> getReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(reportService.getReport(start, end));
    }

    @Operation(summary = "Get total accumulated balance")
    @GetMapping("/balance")
    public ResponseEntity<Map<String, BigDecimal>> getBalance() {
        return ResponseEntity.ok(reportService.getTotalBalance());
    }

    @Operation(summary = "Get revenue by category")
    @GetMapping("/category-revenue")
    public ResponseEntity<List<Map<String, Object>>> getCategoryRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(reportService.getCategoryRevenue(start, end));
    }

    @Operation(summary = "Get expenses by category")
    @GetMapping("/category-expenses")
    public ResponseEntity<List<Map<String, Object>>> getCategoryExpenses(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(reportService.getCategoryExpenses(start, end));
    }

    @Operation(summary = "Get profit by category")
    @GetMapping("/category-profit")
    public ResponseEntity<List<Map<String, Object>>> getCategoryProfit(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(reportService.getCategoryProfit(start, end));
    }
}