package com.radiadorespinheiro.expense.repository;

import com.radiadorespinheiro.expense.domain.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByDateBetween(LocalDate start, LocalDate end);
    List<Expense> findByCategoryId(Long categoryId);
    List<Expense> findByDateBetweenAndCategoryId(LocalDate start, LocalDate end, Long categoryId);

    @Query("SELECT COALESCE(SUM(e.value), 0) FROM Expense e WHERE e.date BETWEEN :start AND :end")
    BigDecimal sumValueBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT COALESCE(SUM(e.value), 0) FROM Expense e")
    BigDecimal sumValue();
}