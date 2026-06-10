package com.eng.software.mova.application.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Requisição para redefinição de senha")
public record PasswordRequestDTO(
        @NotBlank(message = "Password is required")
        @Schema(description = "Nova senha do usuário", example = "NovaSenha123", requiredMode = Schema.RequiredMode.REQUIRED)
        String password,

        @NotBlank(message = "Confirm password is required")
        @Schema(description = "Confirmação da nova senha (deve ser idêntica)", example = "NovaSenha123", requiredMode = Schema.RequiredMode.REQUIRED)
        String confirmPassword
) {
}
