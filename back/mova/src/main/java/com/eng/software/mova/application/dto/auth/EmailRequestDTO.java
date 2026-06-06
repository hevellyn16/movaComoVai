package com.eng.software.mova.application.dto.auth;

import jakarta.validation.constraints.Email;

public record EmailRequestDTO(
        @Email
        String email
) {
}
