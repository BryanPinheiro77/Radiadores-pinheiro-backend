package com.radiadorespinheiro.expense.controller;

import com.radiadorespinheiro.expense.domain.ExpenseCategory;
import com.radiadorespinheiro.expense.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Expense Categories", description = "Expense category management")
@RestController
@RequestMapping("/api/expense-categories")
@RequiredArgsConstructor
public class ExpenseCategoryController {

    private final ExpenseService expenseService;

    @Operation(summary = "List all expense categories")
    @GetMapping
    public ResponseEntity<List<ExpenseCategory>> findAll() {
        return ResponseEntity.ok(expenseService.findAllCategories());
    }

    @Operation(summary = "Create expense category")
    @PostMapping
    public ResponseEntity<ExpenseCategory> save(@RequestBody ExpenseCategory category) {
        return ResponseEntity.ok(expenseService.saveCategory(category));
    }

    @Operation(summary = "Update expense category")
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseCategory> update(@PathVariable Long id, @RequestBody ExpenseCategory category) {
        category.setId(id);
        return ResponseEntity.ok(expenseService.saveCategory(category));
    }

    @Operation(summary = "Delete expense category")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        expenseService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}