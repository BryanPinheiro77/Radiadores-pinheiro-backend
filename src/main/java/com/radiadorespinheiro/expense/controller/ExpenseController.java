package com.radiadorespinheiro.expense.controller;

import com.radiadorespinheiro.expense.dto.ExpenseRequest;
import com.radiadorespinheiro.expense.dto.ExpenseResponse;
import com.radiadorespinheiro.expense.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "Expenses", description = "Expense management endpoints")
@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @Operation(summary = "List all expenses")
    @GetMapping
    public ResponseEntity<Page<ExpenseResponse>> findAll(
            @PageableDefault(size = 20, sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(expenseService.findAll(pageable));
    }

    @Operation(summary = "Find expense by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(expenseService.findById(id));
    }

    @Operation(summary = "Filter expenses by date range and/or category")
    @GetMapping("/filter")
    public ResponseEntity<Page<ExpenseResponse>> filter(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(required = false) Long categoryId,
            @PageableDefault(size = 20, sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(expenseService.filter(start, end, categoryId, pageable));
    }

    @Operation(summary = "Create expense")
    @PostMapping
    public ResponseEntity<ExpenseResponse> save(@RequestBody ExpenseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(expenseService.save(request));
    }

    @Operation(summary = "Update expense")
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponse> update(@PathVariable Long id, @RequestBody ExpenseRequest request) {
        return ResponseEntity.ok(expenseService.update(id, request));
    }

    @Operation(summary = "Delete expense")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        expenseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}