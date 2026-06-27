package com.eng.software.mova.application.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Resposta de autenticação contendo o token JWT e dados básicos do usuário")
public record JwtResponse (
        @Schema(description = "Token JWT para autenticação nas rotas protegidas", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token,

        @Schema(description = "UUID do usuário autenticado", example = "550e8400-e29b-41d4-a716-446655440000")
        String id,

        @Schema(description = "Username do usuário", example = "joaosilva")
        String username,

        @Schema(description = "Role do usuário (ROLE_COMMON ou ROLE_ADMIN)", example = "ROLE_COMMON")
        String role,

        @Schema(description = "E-mail do usuário", example = "joao@email.com")
        String email
) {
}
