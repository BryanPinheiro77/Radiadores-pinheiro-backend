package com.radiadorespinheiro.usuario.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest(
        @NotBlank String name,
        @NotBlank String login,
        @NotBlank String password
) {}