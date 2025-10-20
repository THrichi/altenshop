package com.application.altenshop.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRegisterDTO(
        @NotBlank String username,
        @NotBlank String firstname,
        @NotBlank @Email String email,
        @NotBlank String password
) {}
