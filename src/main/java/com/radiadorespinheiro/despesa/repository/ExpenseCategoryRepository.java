package com.radiadorespinheiro.despesa.repository;

import com.radiadorespinheiro.despesa.domain.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Long> {
}