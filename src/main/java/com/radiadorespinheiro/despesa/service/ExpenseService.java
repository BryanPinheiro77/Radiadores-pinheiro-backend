package com.radiadorespinheiro.despesa.service;

import com.radiadorespinheiro.despesa.domain.Expense;
import com.radiadorespinheiro.despesa.domain.ExpenseCategory;
import com.radiadorespinheiro.despesa.dto.ExpenseRequest;
import com.radiadorespinheiro.despesa.dto.ExpenseResponse;
import com.radiadorespinheiro.despesa.repository.ExpenseCategoryRepository;
import com.radiadorespinheiro.despesa.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;

    private ExpenseResponse toResponse(Expense expense) {
        return ExpenseResponse.builder()
                .id(expense.getId())
                .description(expense.getDescription())
                .value(expense.getValue())
                .date(expense.getDate())
                .categoryId(expense.getCategory().getId())
                .categoryName(expense.getCategory().getName())
                .notes(expense.getNotes())
                .build();
    }

    public List<ExpenseResponse> findAll() {
        return expenseRepository.findAll().stream().map(this::toResponse).toList();
    }

    public ExpenseResponse findById(Long id) {
        return toResponse(expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Despesa não encontrada: " + id)));
    }

    public ExpenseResponse save(ExpenseRequest request) {
        ExpenseCategory category = expenseCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada: " + request.getCategoryId()));

        Expense expense = Expense.builder()
                .description(request.getDescription())
                .value(request.getValue())
                .date(request.getDate())
                .category(category)
                .notes(request.getNotes())
                .build();

        return toResponse(expenseRepository.save(expense));
    }

    public ExpenseResponse update(Long id, ExpenseRequest request) {
        Expense existing = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Despesa não encontrada: " + id));

        ExpenseCategory category = expenseCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada: " + request.getCategoryId()));

        existing.setDescription(request.getDescription());
        existing.setValue(request.getValue());
        existing.setDate(request.getDate());
        existing.setCategory(category);
        existing.setNotes(request.getNotes());

        return toResponse(expenseRepository.save(existing));
    }

    public void delete(Long id) {
        expenseRepository.deleteById(id);
    }

    public List<ExpenseResponse> findByPeriod(LocalDate start, LocalDate end) {
        return expenseRepository.findByDateBetween(start, end).stream().map(this::toResponse).toList();
    }

    public List<ExpenseResponse> findByCategory(Long categoryId) {
        return expenseRepository.findByCategoryId(categoryId).stream().map(this::toResponse).toList();
    }

    public List<ExpenseResponse> findByPeriodAndCategory(LocalDate start, LocalDate end, Long categoryId) {
        return expenseRepository.findByDateBetweenAndCategoryId(start, end, categoryId).stream().map(this::toResponse).toList();
    }

    // --- CRUD ExpenseCategory ---

    public List<ExpenseCategory> findAllCategories() {
        return expenseCategoryRepository.findAll();
    }

    public ExpenseCategory saveCategory(ExpenseCategory category) {
        return expenseCategoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        expenseCategoryRepository.deleteById(id);
    }
}