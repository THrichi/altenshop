package com.application.altenshop.dtos;

public record LoginResponseDTO(
        String token,
        UserResponseDTO user
) {}
