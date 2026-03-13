package com.radiadorespinheiro.despesa.repository;

import com.radiadorespinheiro.despesa.domain.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByDateBetween(LocalDate start, LocalDate end);
    List<Expense> findByCategoryId(Long categoryId);
    List<Expense> findByDateBetweenAndCategoryId(LocalDate start, LocalDate end, Long categoryId);
}