package com.radiadorespinheiro.produto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductRequest (
    @NotBlank String name,
    String description,
    @NotNull  BigDecimal costPrice,
    @NotNull BigDecimal salePrice,
    @NotNull Integer stock,
    @NotNull Integer minStock,
    Long categoryId
){}
