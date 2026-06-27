package com.eng.software.mova.application.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Schema(description = "Perfil público do usuário — exibe apenas informações não sensíveis")
public record UserPublicProfileDTO(
        @Schema(description = "ID único do usuário", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Nome completo", example = "João da Silva")
        String name,

        @Schema(description = "Nome de usuário", example = "joaosilva")
        String username,

        @Schema(description = "URL da foto de perfil", example = "http://localhost:8080/uploads/avatars/avatar.png")
        String avatarUrl,

        @Schema(description = "Biografia do usuário", example = "Amante de eventos culturais e música ao vivo 🎵")
        String bio,

        @Schema(description = "Localização do usuário", example = "São Paulo, SP")
        String location,

        @Schema(description = "Data de criação da conta", example = "2025-01-15T10:30:00")
        LocalDateTime createdAt) {
}
