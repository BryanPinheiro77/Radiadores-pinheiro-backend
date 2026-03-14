package com.radiadorespinheiro.despesa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseRequest {
    private String description;
    private BigDecimal value;
    private LocalDate date;
    private Long categoryId;
    private String notes;
}