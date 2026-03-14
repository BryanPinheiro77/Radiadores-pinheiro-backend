package com.radiadorespinheiro.expense.repository;

import com.radiadorespinheiro.expense.domain.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Long> {
}