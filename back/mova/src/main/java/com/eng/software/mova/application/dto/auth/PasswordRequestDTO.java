package com.eng.software.mova.application.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record PasswordRequestDTO(
        @NotBlank(message = "Password is required")
        String password,
        @NotBlank(message = "Confirm password is required")
        String confirmPassword
) {
}
