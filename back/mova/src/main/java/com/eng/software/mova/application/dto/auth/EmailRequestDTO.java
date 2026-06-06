package com.eng.software.mova.application.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailRequestDTO(
        @NotBlank(message = "Email is required")
        @Email
        String email
) {
}
