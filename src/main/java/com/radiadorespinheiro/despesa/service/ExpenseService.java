package com.radiadorespinheiro.despesa.service;

import com.radiadorespinheiro.despesa.domain.Expense;
import com.radiadorespinheiro.despesa.domain.ExpenseCategory;
import com.radiadorespinheiro.despesa.repository.ExpenseCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;

    // --- CRUD Expense ---

    public List<Expense> findAll() {
        return expenseRepository.findAll();
    }

    public Expense findById(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Despesa não encontrada: " + id));
    }

    public Expense save(Expense expense) {
        return expenseRepository.save(expense);
    }

    public Expense update(Long id, Expense updated) {
        Expense existing = findById(id);
        existing.setDescription(updated.getDescription());
        existing.setValue(updated.getValue());
        existing.setDate(updated.getDate());
        existing.setCategory(updated.getCategory());
        existing.setNotes(updated.getNotes());
        return expenseRepository.save(existing);
    }

    public void delete(Long id) {
        expenseRepository.deleteById(id);
    }

    // --- Filtros ---

    public List<Expense> findByPeriod(LocalDate start, LocalDate end) {
        return expenseRepository.findByDateBetween(start, end);
    }

    public List<Expense> findByCategory(Long categoryId) {
        return expenseRepository.findByCategoryId(categoryId);
    }

    public List<Expense> findByPeriodAndCategory(LocalDate start, LocalDate end, Long categoryId) {
        return expenseRepository.findByDateBetweenAndCategoryId(start, end, categoryId);
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