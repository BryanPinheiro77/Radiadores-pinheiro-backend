package com.radiadorespinheiro.usuario.dto;

public record UserResponse(Long id, String name, String login, boolean active) {}