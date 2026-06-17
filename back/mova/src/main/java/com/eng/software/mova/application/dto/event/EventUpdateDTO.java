package com.eng.software.mova.application.dto.event;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Schema(description = "Payload para atualização parcial de um evento existente. Apenas os campos informados serão atualizados.")
public record EventUpdateDTO(
        @Schema(
            description = "Novo nome do evento. Omita para não alterar.",
            example = "Festival de Jazz na Praça — Edição Especial"
        )
        String eventName,

        @Schema(
            description = "Nova descrição do evento. Omita para não alterar.",
            example = "Edição especial com artistas convidados internacionais."
        )
        String description,

        @Schema(
            description = "Nova classificação indicativa de conteúdo (ex: 'livre', '12', '16', '18'). Omita para não alterar.",
            example = "12"
        )
        String contentRating,

        @Schema(
            description = "Novo preço de entrada do evento. Use 0.00 para tornar gratuito. Omita para não alterar.",
            example = "50.00"
        )
        BigDecimal price,

        @Schema(
            description = "Nova data e hora de início do evento no formato ISO 8601. Omita para não alterar.",
            example = "2025-07-11T19:00:00"
        )
        LocalDateTime startsAt,

        @Schema(
            description = "Nova data e hora de encerramento do evento no formato ISO 8601. Omita para não alterar.",
            example = "2025-07-11T23:30:00"
        )
        LocalDateTime endsAt,

        @Schema(
            description = "Identificador único (UUID) do novo local do evento. Omita para não alterar.",
            example = "c3d4e5f6-a7b8-9012-cdef-123456789abc"
        )
        UUID venueId,

        @Schema(
            description = "Novo conjunto de identificadores únicos (UUIDs) das tags do evento. Substitui completamente as tags anteriores. Omita para não alterar.",
            example = "[\"d1e2f3a4-b5c6-7890-abcd-111111111111\"]"
        )
        Set<UUID> tagIds
) {
}
