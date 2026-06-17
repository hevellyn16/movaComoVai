package com.eng.software.mova.application.dto.event;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Schema(description = "Dados retornados ao consultar ou manipular um evento")
public record EventResponseDTO(
        @Schema(
            description = "Identificador único do evento",
            example = "b0e1d2c3-f4a5-9678-efab-cd1234567890"
        )
        UUID id,

        @Schema(
            description = "Nome do evento",
            example = "Festival de Jazz na Praça"
        )
        String eventName,

        @Schema(
            description = "Descrição detalhada do evento",
            example = "Uma noite incrível de jazz ao ar livre com bandas locais."
        )
        String description,

        @Schema(
            description = "Classificação indicativa de conteúdo do evento",
            example = "livre"
        )
        String contentRating,

        @Schema(
            description = "Preço de entrada do evento",
            example = "25.00"
        )
        BigDecimal price,

        @Schema(
            description = "Data e hora de início do evento",
            example = "2025-07-10T19:00:00"
        )
        LocalDateTime startsAt,

        @Schema(
            description = "Data e hora de encerramento do evento",
            example = "2025-07-10T23:00:00"
        )
        LocalDateTime endsAt,

        @Schema(
            description = "Identificador único do usuário criador do evento",
            example = "a1b2c3d4-e5f6-7890-abcd-111122223333"
        )
        UUID creatorId,

        @Schema(
            description = "Identificador único do local onde o evento é realizado",
            example = "c3d4e5f6-a7b8-9012-cdef-123456789abc"
        )
        UUID venueId,

        @Schema(
            description = "Nome do local onde o evento é realizado",
            example = "Praça Central"
        )
        String venueName,

        @Schema(
            description = "Conjunto de nomes das tags associadas ao evento",
            example = "[\"música\", \"jazz\", \"gratuito\"]"
        )
        Set<String> tags,

        @Schema(
            description = "Conjunto de identificadores únicos dos usuários que curtiram o evento",
            example = "[\"a1b2c3d4-e5f6-7890-abcd-111122223333\"]"
        )
        Set<UUID> likedByUserIds
) {
}
