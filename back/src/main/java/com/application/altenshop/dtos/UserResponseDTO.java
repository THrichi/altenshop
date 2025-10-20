package com.application.altenshop.dtos;

public record UserResponseDTO(
        Long id,
        String username,
        String firstname,
        String email
) {}
