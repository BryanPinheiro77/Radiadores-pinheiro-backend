package com.radiadorespinheiro.expense.service;

import com.radiadorespinheiro.expense.domain.Expense;
import com.radiadorespinheiro.expense.domain.ExpenseCategory;
import com.radiadorespinheiro.expense.domain.ExpenseType;
import com.radiadorespinheiro.expense.dto.ExpenseRequest;
import com.radiadorespinheiro.expense.dto.ExpenseResponse;
import com.radiadorespinheiro.expense.repository.ExpenseCategoryRepository;
import com.radiadorespinheiro.expense.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;

    @Transactional(readOnly = true)
    public Page<ExpenseResponse> findAll(Pageable pageable) {
        pageable = normalizePageable(pageable);
        return expenseRepository.findAll(pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ExpenseResponse findById(Long id) {
        return toResponse(expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Despesa não encontrada: " + id)));
    }

    @CacheEvict(value = "dashboard-summary", allEntries = true)
    public ExpenseResponse save(ExpenseRequest request) {
        ExpenseCategory category = expenseCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        if (request.getExpenseType() == ExpenseType.INSTALLMENT && request.getTotalInstallments() != null) {
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

    @CacheEvict(value = "dashboard-summary", allEntries = true)
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

    @CacheEvict(value = "dashboard-summary", allEntries = true)
    public void delete(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Despesa não encontrada: " + id));
        expenseRepository.delete(expense);
    }

    @Transactional(readOnly = true)
    public Page<ExpenseResponse> filter(LocalDate start, LocalDate end, Long categoryId, Pageable pageable) {
        pageable = normalizePageable(pageable);

        if (start != null && end != null && categoryId != null) {
            return expenseRepository.findByDateBetweenAndCategory_Id(start, end, categoryId, pageable)
                    .map(this::toResponse);
        } else if (start != null && end != null) {
            return expenseRepository.findByDateBetween(start, end, pageable)
                    .map(this::toResponse);
        } else if (categoryId != null) {
            return expenseRepository.findByCategory_Id(categoryId, pageable)
                    .map(this::toResponse);
        }

        return expenseRepository.findAll(pageable)
                .map(this::toResponse);
    }

    public List<ExpenseCategory> findAllCategories() {
        return expenseCategoryRepository.findAll();
    }

    public ExpenseCategory saveCategory(ExpenseCategory category) {
        return expenseCategoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        expenseCategoryRepository.deleteById(id);
    }

    private Pageable normalizePageable(Pageable pageable) {
        int maxSize = 50;
        int size = Math.min(pageable.getPageSize(), maxSize);

        return PageRequest.of(
                pageable.getPageNumber(),
                size,
                pageable.getSort()
        );
    }

    private ExpenseResponse toResponse(Expense expense) {
        return ExpenseResponse.builder()
                .id(expense.getId())
                .description(expense.getDescription())
                .value(expense.getValue())
                .date(expense.getDate())
                .categoryId(expense.getCategory() != null ? expense.getCategory().getId() : null)
                .categoryName(expense.getCategory() != null ? expense.getCategory().getName() : null)
                .notes(expense.getNotes())
                .build();
    }
}