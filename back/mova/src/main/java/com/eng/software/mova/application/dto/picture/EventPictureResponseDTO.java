package com.eng.software.mova.application.dto.picture;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.UUID;

@Builder
@Schema(description = "Dados retornados ao consultar ou manipular uma imagem vinculada a um evento")
public record EventPictureResponseDTO(
        @Schema(
            description = "Identificador único da imagem do evento",
            example = "f1e2d3c4-b5a6-7890-abcd-ef1234567890"
        )
        UUID id,

        @Schema(
            description = "Identificador único do evento ao qual a imagem está vinculada",
            example = "b0e1d2c3-f4a5-9678-efab-cd1234567890"
        )
        UUID eventId,

        @Schema(
            description = "URL pública de acesso à imagem armazenada",
            example = "https://storage.example.com/events/banner01.jpg"
        )
        String pictureUrl
) {
}
