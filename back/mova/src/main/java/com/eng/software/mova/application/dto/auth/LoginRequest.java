package com.eng.software.mova.application.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Credenciais de login do usuário")
public record LoginRequest(
        @Schema(description = "E-mail ou username do usuário", example = "joao@email.com", requiredMode = Schema.RequiredMode.REQUIRED)
        String username,

        @Schema(description = "Senha do usuário", example = "Senha123", requiredMode = Schema.RequiredMode.REQUIRED)
        String password
) {
}
