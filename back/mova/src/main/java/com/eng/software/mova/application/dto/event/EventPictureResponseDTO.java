package com.eng.software.mova.application.dto.event;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Dados de uma imagem associada a um evento")
public record EventPictureResponseDTO(
        @Schema(description = "UUID da imagem", example = "770e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "URL da imagem", example = "https://exemplo.com/imagens/evento-rock.jpg")
        String pictureUrl
) {
}
