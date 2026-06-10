package com.eng.software.mova.application.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Requisição contendo o e-mail do usuário")
public record EmailRequestDTO(
        @NotBlank(message = "Email is required")
        @Email
        @Schema(description = "Endereço de e-mail do usuário", example = "joao@email.com", requiredMode = Schema.RequiredMode.REQUIRED)
        String email
) {
}
