package com.eng.software.mova.application.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Credenciais de login do usuário")
public record LoginRequest(
        @NotBlank(message = "Username or email must not be blank")
        @Schema(description = "E-mail ou username do usuário", example = "joao@email.com", requiredMode = Schema.RequiredMode.REQUIRED)
        String username,

        @NotBlank(message = "Password must not be blank")
        @Schema(description = "Senha do usuário", example = "Senha123", requiredMode = Schema.RequiredMode.REQUIRED)
        String password
) {
}
