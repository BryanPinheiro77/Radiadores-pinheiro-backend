package com.radiadorespinheiro.expense.controller;

import com.radiadorespinheiro.despesa.domain.Expense;
import com.radiadorespinheiro.despesa.domain.ExpenseCategory;
import com.radiadorespinheiro.despesa.dto.ExpenseRequest;
import com.radiadorespinheiro.despesa.dto.ExpenseResponse;
import com.radiadorespinheiro.despesa.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> findAll() {
        return ResponseEntity.ok(expenseService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(expenseService.findById(id));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<ExpenseResponse>> filter(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(required = false) Long categoryId) {

        if (start != null && end != null && categoryId != null) {
            return ResponseEntity.ok(expenseService.findByPeriodAndCategory(start, end, categoryId));
        } else if (start != null && end != null) {
            return ResponseEntity.ok(expenseService.findByPeriod(start, end));
        } else if (categoryId != null) {
            return ResponseEntity.ok(expenseService.findByCategory(categoryId));
        }
        return ResponseEntity.ok(expenseService.findAll());
    }

    @PostMapping
    public ResponseEntity<ExpenseResponse> save(@RequestBody ExpenseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(expenseService.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponse> update(@PathVariable Long id, @RequestBody ExpenseRequest request) {
        return ResponseEntity.ok(expenseService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        expenseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}