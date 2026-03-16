package com.radiadorespinheiro.expense.service;

import com.radiadorespinheiro.expense.domain.Expense;
import com.radiadorespinheiro.expense.domain.ExpenseCategory;
import com.radiadorespinheiro.expense.domain.ExpenseType;
import com.radiadorespinheiro.expense.dto.ExpenseRequest;
import com.radiadorespinheiro.expense.dto.ExpenseResponse;
import com.radiadorespinheiro.expense.repository.ExpenseCategoryRepository;
import com.radiadorespinheiro.expense.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
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
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        if (request.getExpenseType() == ExpenseType.INSTALLMENT && request.getTotalInstallments() != null) {
            // Cria todas as parcelas
            List<Expense> installments = new ArrayList<>();
            for (int i = 1; i <= request.getTotalInstallments(); i++) {
                Expense expense = Expense.builder()
                        .description(request.getDescription() + " (" + i + "/" + request.getTotalInstallments() + ")")
                        .value(request.getValue())
                        .date(request.getDate().plusMonths(i - 1))
                        .category(category)
                        .notes(request.getNotes())
                        .expenseType(ExpenseType.INSTALLMENT)
                        .totalInstallments(request.getTotalInstallments())
                        .currentInstallment(i)
                        .build();
                installments.add(expense);
            }
            expenseRepository.saveAll(installments);
            return toResponse(installments.get(0));
        }

        Expense expense = Expense.builder()
                .description(request.getDescription())
                .value(request.getValue())
                .date(request.getDate())
                .category(category)
                .notes(request.getNotes())
                .expenseType(request.getExpenseType() != null ? request.getExpenseType() : ExpenseType.SINGLE)
                .totalInstallments(null)
                .currentInstallment(null)
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