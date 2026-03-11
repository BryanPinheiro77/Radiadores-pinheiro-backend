package com.radiadorespinheiro.categoria.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
        String name,
        String description
) {}