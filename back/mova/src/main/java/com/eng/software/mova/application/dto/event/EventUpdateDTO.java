package com.eng.software.mova.application.dto.event;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Schema(description = "Dados para atualização de um evento. Todos os campos são opcionais — envie apenas os que deseja alterar.")
public record EventUpdateDTO(
        @Schema(description = "Novo nome do evento", example = "Show de Rock Atualizado")
        String eventName,

        @Schema(description = "Nova descrição do evento", example = "Descrição atualizada do evento.")
        String description,

        @Schema(description = "Nova classificação indicativa", example = "12+")
        String contentRating,

        @Schema(description = "Novo preço do ingresso em reais", example = "75.00")
        BigDecimal price,

        @Schema(description = "Nova data e hora de início (ISO 8601)", example = "2025-09-01T20:00:00")
        LocalDateTime startsAt,

        @Schema(description = "Nova data e hora de término (ISO 8601)", example = "2025-09-02T02:00:00")
        LocalDateTime endsAt,

        @Schema(description = "UUID do novo local (venue)", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID venueId,

        @Schema(description = "IDs das tags a serem associadas", example = "[\"550e8400-e29b-41d4-a716-446655440000\"]")
        Set<UUID> tagIds
) {
}
