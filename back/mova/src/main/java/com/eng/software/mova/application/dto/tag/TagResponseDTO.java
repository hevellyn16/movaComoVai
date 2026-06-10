package com.eng.software.mova.application.dto.tag;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.UUID;

@Builder
@Schema(description = "Dados de uma tag (resposta)")
public record TagResponseDTO(
        @Schema(description = "UUID da tag", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Nome da tag", example = "Música Ao Vivo")
        String tagName
) {
}
